import { describe, it, expect, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/svelte';
import Dialog from './Dialog.svelte';

describe('Dialog Component', () => {
    const mockDialog = {
        open: false,
        close: () => {
            mockDialog.open = false;
        }
    };

    beforeEach(() => {
        mockDialog.open = false;
    });

    it('should render with title', () => {
        const title = 'Test Dialog';
        render(Dialog, { 
            props: { 
                dialog: mockDialog,
                title: title
            }
        });
        
        expect(screen.getByText(title)).toBeTruthy();
    });
});
