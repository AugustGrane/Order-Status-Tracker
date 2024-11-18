import { writeFile } from 'fs/promises';

export const actions = {
    upload: async ({ cookies, request, locals, fetch }) => {  // Added 'fetch' here

        const data = await request.formData();
        let image: File;

        // Check cont
        if (data.get('image') == null) {
            return { success: false }; // error
        } else {
            image = data.get('image') as File;
        }
        // Write the image content to a file
        await writeFile(`./static/uploads/${image.name}`, new Uint8Array(await image.arrayBuffer()));


        let name = data.get('name'); // value of 'name' attribute of input;
        let description = data.get('description'); // value of 'description' attribute of input

        // create image path for sending to database
        const image_path = `frontend/static/uploads/${image.name}`;

        // Use event.fetch for server-side HTTP requests in SvelteKit
        const response = await fetch('/api/status-definitions', {  // Now using event.fetch
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ name, description, image: image_path }),
        });

        return { success: true };
    }
};