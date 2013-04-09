// Define an friendly no op console log if there is none supported in the browser 
// (IE7 for example)
if (typeof console == "undefined" || typeof console.log == "undefined") {
   var console = { log: function() {} }; 
}