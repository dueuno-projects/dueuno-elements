<render:component instance="${c}"/>
<g:if test="${c.inputType == 'password'}">
    <button class="show-password btn btn-secondary show-hide-password" type="button">
        <i class="fa-solid fa-eye"></i>
    </button>
</g:if>
