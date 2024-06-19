const sleep = (delay) => new Promise((resolve) => setTimeout(resolve, delay));

function getOS() {
  var uA = navigator.userAgent || navigator.vendor || window.opera;
  if ((/iPad|iPhone|iPod/.test(uA) && !window.MSStream) || (uA.includes('Mac') && 'ontouchend' in document)) return 'iOS';

  var i, os = ['Windows', 'Android', 'Unix', 'Mac', 'Linux', 'BlackBerry'];
  for (i = 0; i < os.length; i++) if (new RegExp(os[i],'i').test(uA)) return os[i];
}

function isUndefined(value) {
    return typeof value === 'undefined';
}

function capitalize(val) {
    return val.charAt(0).toUpperCase() + val.slice(1);
}

function isEmpty(obj) {
    if (obj == null) return true;
    if (obj.length > 0) return false;
    if (obj.length === 0) return true;
    if (typeof obj !== "object") return true;
    for (key in obj) {
        if (obj.hasOwnProperty(key)) return false;
    }
    return true;
}
