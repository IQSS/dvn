
// SCRIPT: Login form, vertically align center
window.onload   = initPage;
window.onresize = initPage;

function initPage() {
	
  // Get paragraph height
  var paragraph =  document.getElementById('loginPageWrap');
  var paragraphHeight   = paragraph.offsetHeight;
  
  // Get browser window height
  var windowHeight = getWindowHeight();

  // Set paragraph area on page
  paragraph.style.position = 'absolute';
  paragraph.style.top = ((windowHeight - paragraphHeight) / 2) + 'px';
}

function getWindowHeight() {
  var windowHeight = 0;
	
  if (typeof(window.innerHeight) == 'number')
    windowHeight = window.innerHeight;
  else {
    if (document.documentElement && document.documentElement.clientHeight)
      windowHeight = document.documentElement.clientHeight;
    else {
      if (document.body && document.body.clientHeight)
        windowHeight = document.body.clientHeight; }; };
	
  return windowHeight;
};
// END SCRIPT: Login form, vertically align center