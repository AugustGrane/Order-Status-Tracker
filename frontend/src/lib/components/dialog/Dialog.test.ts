import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/svelte';
import Dialog from './Dialog.svelte';

describe('Dialog', () => {
    it('should render', () => {
        const { container } = render(Dialog, { props: { open: false } });
        expect(container).toBeTruthy();
    });
});
