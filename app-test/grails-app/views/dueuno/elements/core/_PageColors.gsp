<style>
    :root {
        --elements-primary-bg: ${c.primaryBackgroundColorInt.join(', ')};
        --elements-primary-bg-alpha: ${c.primaryBackgroundColorAlpha};
        --elements-primary-bg-shaded: ${c.primaryBackgroundColorInt.join(', ')}, ${c.primaryBackgroundColorAlpha};
        --elements-primary-text: ${c.primaryTextColorInt.join(', ')};
        --elements-secondary-bg: ${c.secondaryBackgroundColorInt.join(', ')};
        --elements-secondary-text: ${c.secondaryTextColorInt.join(', ')};
        --elements-tertiary-bg: ${c.tertiaryBackgroundColorInt.join(', ')};
        --elements-tertiary-text: ${c.tertiaryTextColorInt.join(', ')};
    }
</style>