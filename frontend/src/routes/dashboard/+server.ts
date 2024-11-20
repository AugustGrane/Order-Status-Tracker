import path from "path";
import {readdir} from "fs/promises";
import {json} from "@sveltejs/kit";

export async function GET() {
    try {
        const uploadsDir = path.resolve('./static/uploads'); // Adjust path if necessary
        const files = await readdir(uploadsDir);
        return json({ success: true, files });
    } catch (error) {
        console.error('Error fetching uploads:', error);
        return json({ success: false, error: 'Failed to fetch uploaded files.' }, { status: 500 });
    }
}