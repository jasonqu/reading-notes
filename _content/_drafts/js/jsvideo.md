js video

http://segmentfault.com/blog/animabear/1190000000380064

https://developer.mozilla.org/zh-CN/docs/DOM/Using_fullscreen_mode


jsvideo


http://stackoverflow.com/questions/12822739/full-screen-api-html5-and-safari-ios-6

I'm trying to make an application to run in full screen mode (without the top bar) in Safari for iOS 6. The code is as follows:

var elem = document.getElementById("element_id");
if (elem.requestFullScreen) {
  elem.requestFullScreen();
} else if (elem.mozRequestFullScreen) {
  elem.mozRequestFullScreen();
} else if (elem.webkitRequestFullScreen) {
  elem.webkitRequestFullScreen();
}


It's not supported...

http://caniuse.com/fullscreen


thank god ,there is a solution.

https://developer.apple.com/library/safari/documentation/AudioVideo/Conceptual/Using_HTML5_Audio_Video/Introduction/Introduction.html

in here
https://developer.apple.com/library/safari/documentation/AudioVideo/Conceptual/Using_HTML5_Audio_Video/ControllingMediaWithJavaScript/ControllingMediaWithJavaScript.html#//apple_ref/doc/uid/TP40009523-CH3-SW13

