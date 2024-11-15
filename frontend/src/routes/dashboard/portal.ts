// Portal action to render content at the document body level
export function portal(node: HTMLElement) {
    // Create a wrapper div for the portal
    const portal = document.createElement('div');
    portal.className = 'portal-container';
    
    document.body.appendChild(portal);
    portal.appendChild(node);

    return {
        destroy() {
            if (document.body.contains(portal)) {
                document.body.removeChild(portal);
            }
        }
    };
}
