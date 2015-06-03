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


iPhone Video Placeholder

On iPhone and iPod touch, a placeholder with a play button is shown until the user initiates playback, as shown in Figure 2-1. The placeholder is translucent, so the background or any poster image shows through. The placeholder provides a way for the user to play the media. If the iOS device cannot play the specified media, there is a diagonal bar through the control, indicating that it cannot play.

On the desktop and iPad, the first frame of a video displays as soon as it becomes available. There is no placeholder.



防止用户拖拽？
iosvideo支持timeupdate和seek事件，而且支持设定时间
需要做的是update的时候不要把seek的情况给update了
http://stackoverflow.com/questions/9483542/is-it-possible-to-restrict-forwarding-in-html5-video

http://blog.millermedeiros.com/html5-video-issues-on-the-ipad-and-how-to-solve-them/

http://www.html5rocks.com/en/tutorials/video/basics/

fiddler
http://www.leggetter.co.uk/2010/03/19/using-fiddler-to-help-develop-cross-domain-capable-javascript-web-applications.html



https://developer.apple.com/library/safari/documentation/AudioVideo/Conceptual/Using_HTML5_Audio_Video/ControllingMediaWithJavaScript/ControllingMediaWithJavaScript.html#//apple_ref/doc/uid/TP40009523-CH3-SW4

http://www.ibm.com/developerworks/cn/web/wa-ioshtml5/

https://gist.github.com/millermedeiros/891886

http://stackoverflow.com/questions/10235919/the-canplay-canplaythrough-events-for-an-html5-video-are-not-called-on-firefox

http://www.w3cschool.cc/tags/av-event-canplaythrough.html












