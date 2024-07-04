$.fn.exists = function () {
    return this.length !== 0;
}

$.fn.attrIs = function(name, value) {
    return (typeof this.data(name) != 'undefined' && this.data(name) == value);
};

$.fn.attrIsTrue = function(name) {
    return (typeof this.data(name) != 'undefined' && this.data(name) == true);
};

$.fn.attrIsFalse = function(name) {
    return (typeof this.data(name) == 'undefined' || this.data(name) == false);
};

$.fn.attrIsNot = function(name, value) {
    return (typeof this.data(name) == 'undefined' || this.data(name) != value);
};

$.fn.attrExists = function(name) {
    return (typeof this.data(name) != 'undefined' && this.data(name) != '');
};

$.fn.removeClassesStartingWith = function(string) {
    $(this).removeClass (function (index, className) {
        return (className.match ( new RegExp("\\b"+ string +"\\S+", "g") ) || []).join(' ');
    });
};