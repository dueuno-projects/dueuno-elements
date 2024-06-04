<nav id="shell-navbar"
     class="shell-navbar navbar fixed-top z-1"
     style="${c.shell.config.display.menu ? '' : 'margin-left: 0 !important;'}"
>
    <div id="shell-navbar-menu-toggle">
        <button type="button"
                class="btn btn-secondary text-dark"
                data-bs-toggle="offcanvas"
                data-bs-target="#shell-menu"
                aria-controls="offcanvasResponsive">
            <i class="fa-solid fa-bars"></i>
        </button>
    </div>

    <g:if test="${c.shell.config.display.homeButton}">
        <div id="shell-navbar-home">
            <render:component instance="${c.home}" />
        </div>
    </g:if>

    <img id="shell-navbar-logo"
        class="mx-auto"
        src="${c.shell.config.display.logo}"
    />

<!--    <div class="shell-navbar-notifications-toggle">-->
<!--        <button type="button" class="btn btn-secondary text-dark"-->
<!--                data-bs-toggle="offcanvas" data-bs-target="#shell-user-menu">-->
<!--            <div class="" style="color: white; background-color: #cc0000;">3 <i class="fa-solid fa-bell"></i></div>-->
<!--        </button>-->
<!--    </div>-->

    <g:if test="${c.shell.config.display.userMenu}">
        <div id="shell-navbar-user-menu-toggle">
            <button type="button" class="btn btn-secondary text-dark"
                    data-bs-toggle="offcanvas" data-bs-target="#shell-user-menu">
                <i class="fa-solid fa-circle-user"></i>
            </button>
        </div>
    </g:if>
</nav>

