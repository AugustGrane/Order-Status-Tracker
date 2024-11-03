# Database Development with Neon Branches

## Overview
We use Neon Postgres for our database with a smart branch management system. This allows developers to easily switch between different database branches using environment variables.

## Setup

1. Get your Neon API key:
   - Go to [Neon Console](https://console.neon.tech)
   - Click on your profile -> API Keys
   - Generate a new API key

2. Set up your environment variables in `.env`:
```
NEON_API_KEY=your_neon_api_key
NEON_PROJECT_ID=your_project_id
NEON_DB_USERNAME=your_username
NEON_DB_PASSWORD=your_password
NEON_DB_URL=jdbc:postgresql://<branch-hostname>/gtrykdb?sslmode=require
```

3. Install script dependencies:
```bash
cd scripts
npm install
```

## Using the Branch Manager

Run the branch manager script:
```bash
cd scripts
node switch-db-branch.js
```

The script provides these options:
1. Switch to an existing branch
2. Create a new branch
3. List all branches
4. Exit

The script will automatically update your `.env` file with the correct database URL for your chosen branch.

## How It Works

1. The script uses the Neon API to manage database branches
2. When you switch branches, it updates the `NEON_DB_URL` in your `.env` file
3. Spring Boot reads the database configuration from environment variables
4. No manual configuration needed - just switch branches and restart your application

## Benefits of This Approach

1. **Simple Branch Switching**: One command to switch database branches
2. **Environment Management**: Automatically updates your environment configuration
3. **No Manual Configuration**: No need to modify application properties
4. **Version Control Safe**: All sensitive information stays in `.env`
5. **Development Isolation**: Each developer can work with their own database branch
6. **API-Based**: No need for additional CLI tools

## Best Practices

1. Create a new branch for each feature you're working on
2. Name branches descriptively (e.g., `feature-user-auth`, `fix-order-status`)
3. Delete branches after merging features
4. Regularly reset development branches from production to stay in sync

## Troubleshooting

If you encounter issues:

1. Verify your environment variables:
```bash
cat .env
```

2. Check your Neon API access:
```bash
curl -H "Authorization: Bearer $NEON_API_KEY" \
     https://console.neon.tech/api/v2/projects/$NEON_PROJECT_ID/branches
```

3. Ensure Spring Boot is picking up your environment variables:
```bash
./mvnw spring-boot:run -X
```

4. If the script fails:
   - Check that you're in the `scripts` directory when running it
   - Verify that all dependencies are installed
   - Ensure your `.env` file is in the project root directory
