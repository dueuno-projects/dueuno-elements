<style>
    :root, html {
        --elements-primary-bg: ${c.primaryBackgroundColorInt.join(', ')};
        --elements-primary-bg-alpha: ${c.primaryBackgroundColorAlpha};
        --elements-primary-bg-shaded: ${c.primaryBackgroundColorInt.join(', ')}, ${c.primaryBackgroundColorAlpha};
        --elements-primary-text: ${c.primaryTextColorInt.join(', ')};
        --elements-secondary-bg: ${c.secondaryBackgroundColorInt.join(', ')};
        --elements-secondary-text: ${c.secondaryTextColorInt.join(', ')};
        --elements-main-bg: ${c.mainBackgroundColorInt.join(', ')};
        --elements-main-fg: ${c.mainForegroundColorInt.join(', ')};
        --elements-main-text: ${c.mainTextColorInt.join(', ')};
        --bs-body-color: rgb(var(--elements-main-text));
    }
</style>