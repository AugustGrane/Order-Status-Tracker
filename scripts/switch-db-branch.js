const fs = require('fs');
const fetch = require('node-fetch');
const readline = require('readline');
require('dotenv').config({ path: '../.env' });

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});

const NEON_API_KEY = process.env.NEON_API_KEY;
const PROJECT_ID = process.env.NEON_PROJECT_ID;

if (!NEON_API_KEY || !PROJECT_ID) {
  console.error('Error: NEON_API_KEY and NEON_PROJECT_ID must be set in .env');
  process.exit(1);
}

// Sleep function for adding delays between operations
const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// Function to update .env file
function updateEnvFile(branchHostname) {
  const envPath = '../.env';
  let envContent = '';
  
  try {
    envContent = fs.readFileSync(envPath, 'utf8');
  } catch (error) {
    console.error('No .env file found. Creating new one.');
  }

  const envLines = envContent.split('\n');
  const newEnvLines = [];
  let foundDbUrl = false;

  // Update or add the database URL
  for (const line of envLines) {
    if (line.startsWith('NEON_DB_URL=')) {
      newEnvLines.push(`NEON_DB_URL=jdbc:postgresql://${branchHostname}/gtrykdb?sslmode=require`);
      foundDbUrl = true;
    } else if (line.trim() !== '') {
      newEnvLines.push(line);
    }
  }

  if (!foundDbUrl) {
    newEnvLines.push(`NEON_DB_URL=jdbc:postgresql://${branchHostname}/gtrykdb?sslmode=require`);
  }

  fs.writeFileSync(envPath, newEnvLines.join('\n') + '\n');
}

// Function to list branches using Neon API
async function listBranches() {
  try {
    console.log('Fetching branches...');
    
    const response = await fetch(`https://console.neon.tech/api/v2/projects/${PROJECT_ID}/branches`, {
      headers: {
        'Authorization': `Bearer ${NEON_API_KEY}`,
        'Accept': 'application/json'
      }
    });
    
    if (!response.ok) {
      const errorText = await response.text();
      console.error('Full API Response:', errorText);
      throw new Error(`API request failed: ${response.status} ${response.statusText}`);
    }

    const data = await response.json();
    return data.branches;
  } catch (error) {
    console.error('Error listing branches:', error.message);
    process.exit(1);
  }
}

// Function to create a new branch using Neon API
async function createBranch(name, parentBranchId = null) {
  try {
    console.log('Creating new branch...');
    
    const body = {
      endpoints: [{
        type: 'read_write'
      }],
      branch: {
        name: name
      }
    };

    if (parentBranchId) {
      body.branch.parent_id = parentBranchId;
    }
    
    const response = await fetch(`https://console.neon.tech/api/v2/projects/${PROJECT_ID}/branches`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${NEON_API_KEY}`,
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify(body)
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error('Full API Response:', errorText);
      throw new Error(`API request failed: ${response.status} ${response.statusText}`);
    }

    const data = await response.json();
    return data.branch;
  } catch (error) {
    console.error('Error creating branch:', error.message);
    process.exit(1);
  }
}

// Function to delete a branch
async function deleteBranch(branchId) {
  try {
    console.log('Deleting branch...');
    
    const response = await fetch(`https://console.neon.tech/api/v2/projects/${PROJECT_ID}/branches/${branchId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${NEON_API_KEY}`,
        'Accept': 'application/json'
      }
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error('Full API Response:', errorText);
      throw new Error(`API request failed: ${response.status} ${response.statusText}`);
    }

    return true;
  } catch (error) {
    console.error('Error deleting branch:', error.message);
    process.exit(1);
  }
}

// Function to get main branch ID
async function getMainBranchId() {
  const branches = await listBranches();
  const mainBranch = branches.find(b => b.name === 'main');
  if (!mainBranch) {
    throw new Error('Main branch not found');
  }
  return mainBranch.id;
}

// Function to wait for operations to complete
async function waitForOperations() {
  console.log('Waiting for operations to complete...');
  await sleep(5000); // Wait 5 seconds between operations
}

// Function to reset a branch from main
async function resetBranchFromMain(branchId, branchName) {
  try {
    console.log('Resetting branch from main...');
    
    // Get main branch ID
    const mainBranchId = await getMainBranchId();
    await waitForOperations();
    
    // Delete old branch
    await deleteBranch(branchId);
    await waitForOperations();
    
    // Create new branch with original name from main
    const newBranch = await createBranch(branchName, mainBranchId);
    
    return newBranch;
  } catch (error) {
    console.error('Error resetting branch:', error.message);
    process.exit(1);
  }
}

// Function to get branch endpoint
function getBranchEndpoint(branch) {
  if (branch.endpoints && branch.endpoints.length > 0) {
    return branch.endpoints[0].host;
  }
  // Fallback to constructing the endpoint from the branch ID
  const currentUrl = process.env.NEON_DB_URL;
  const baseHostname = currentUrl.match(/postgresql:\/\/(.*?)\/gtrykdb/)[1];
  const [projectPart, region, provider, domain] = baseHostname.split('.');
  return `${projectPart}-${branch.id}.${region}.${provider}.${domain}`;
}

// Main function
async function main() {
  console.log('🌿 Neon Branch Manager');
  console.log('\nOptions:');
  console.log('1. Switch to existing branch');
  console.log('2. Create new branch');
  console.log('3. List all branches');
  console.log('4. Delete branch');
  console.log('5. Reset branch from main');
  console.log('6. Exit');

  rl.question('Select an option: ', async (answer) => {
    try {
      switch (answer) {
        case '1': {
          const branches = await listBranches();
          console.log('\nAvailable branches:');
          branches.forEach((branch, index) => {
            const endpoint = getBranchEndpoint(branch);
            console.log(`${index + 1}. ${branch.name} (${endpoint})`);
          });

          rl.question('\nSelect branch number: ', (branchNum) => {
            const branch = branches[parseInt(branchNum) - 1];
            if (branch) {
              const endpoint = getBranchEndpoint(branch);
              updateEnvFile(endpoint);
              console.log(`\n✅ Updated .env to use branch: ${branch.name}`);
            } else {
              console.log('Invalid branch selection');
            }
            rl.close();
          });
          break;
        }

        case '2': {
          rl.question('Enter new branch name: ', async (branchName) => {
            const mainBranchId = await getMainBranchId();
            const newBranch = await createBranch(branchName, mainBranchId);
            const endpoint = getBranchEndpoint(newBranch);
            updateEnvFile(endpoint);
            console.log(`\n✅ Created and switched to new branch: ${branchName}`);
            rl.close();
          });
          break;
        }

        case '3': {
          const branches = await listBranches();
          console.log('\nAll branches:');
          branches.forEach(branch => {
            const endpoint = getBranchEndpoint(branch);
            console.log(`- ${branch.name} (${endpoint})`);
          });
          rl.close();
          break;
        }

        case '4': {
          const branches = await listBranches();
          console.log('\nAvailable branches:');
          branches.forEach((branch, index) => {
            console.log(`${index + 1}. ${branch.name}`);
          });

          rl.question('\nSelect branch number to delete: ', async (branchNum) => {
            const branch = branches[parseInt(branchNum) - 1];
            if (branch) {
              if (branch.name === 'main') {
                console.log('❌ Cannot delete main branch');
              } else {
                await deleteBranch(branch.id);
                console.log(`\n✅ Deleted branch: ${branch.name}`);
              }
            } else {
              console.log('Invalid branch selection');
            }
            rl.close();
          });
          break;
        }

        case '5': {
          const branches = await listBranches();
          console.log('\nAvailable branches:');
          branches.forEach((branch, index) => {
            if (branch.name !== 'main') {
              console.log(`${index + 1}. ${branch.name}`);
            }
          });

          rl.question('\nSelect branch number to reset from main: ', async (branchNum) => {
            const branch = branches[parseInt(branchNum) - 1];
            if (branch) {
              if (branch.name === 'main') {
                console.log('❌ Cannot reset main branch');
              } else {
                const newBranch = await resetBranchFromMain(branch.id, branch.name);
                const endpoint = getBranchEndpoint(newBranch);
                updateEnvFile(endpoint);
                console.log(`\n✅ Reset branch ${branch.name} from main and updated connection`);
              }
            } else {
              console.log('Invalid branch selection');
            }
            rl.close();
          });
          break;
        }

        case '6':
          rl.close();
          break;

        default:
          console.log('Invalid option');
          rl.close();
      }
    } catch (error) {
      console.error('Error:', error.message);
      rl.close();
    }
  });
}

main();
