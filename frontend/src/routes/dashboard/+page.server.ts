import { writeFile, readdir } from 'fs/promises';

export const actions = {
    upload: async ({ request, fetch }) => {
        try {
            const data = await request.formData();

            // Validate image input
            const image = data.get('image') as File | null;
            if (!image || !(image instanceof File)) {
                return { success: false, error: 'No valid image file provided' };
            }

            // Create unique image id
            const imageId = new Date().getTime();
            const imagePath: string =  imageId + "_" + image.name;

            // Write the image content to a file
            const filePath = `./static/uploads/${imagePath}`;
            try {
                await writeFile(filePath, new Uint8Array(await image.arrayBuffer()));
            } catch (fileError) {
                console.error('File save error:', fileError);
                return { success: false, error: 'Failed to save the file to the server.' };
            }

            // Extract and validate additional form data
            const name = data.get('name');
            const description = data.get('description');

            if (!name || typeof name !== 'string') {
                return { success: false, error: 'Name is required and must be a string.' };
            }

            if (!description || typeof description !== 'string') {
                return { success: false, error: 'Description is required and must be a string.' };
            }

            const image_full_path = `frontend/static/uploads/${imagePath}`;

            // POST new step/status definition to Spring backend
            try {
                const response = await fetch('/api/create-status-definition', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ name, description, image: image_full_path }),
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    return { success: false, error: `API error: ${errorData.message || 'Unknown error'}` };
                }

                return { success: true };
            } catch (apiError) {
                console.error('API request error:', apiError);
                return { success: false, error: 'Failed to communicate with the API.' };
            }
        } catch (error) {
            console.error('Unexpected error:', error);
            return { success: false, error: 'An unexpected error occurred.' };
        }

    },
    // Define a GET function to list uploaded filenames
};