// Define an friendly no op console log if there is none supported in the browser 
// (IE7 for example)
if (typeof console == "undefined" || typeof console.log == "undefined") {
   var console = { log: function() {} }; 
}

/**
 * The Template Loader. 
 * Used to asynchronously load templates located in separate .html files
 */
window.templateLoader = {
  load: function(views, callback) {
    var deferreds = [];
    $.each(views, function(index, view) {
      if (window[view]) {
        deferreds.push($.get('templates/' + view + '.html', function(data) {
          window[view].prototype.template = _.template(data);
        }, 'html'));
      } else {
        alert(view + " not found");
      }
    });
    $.when.apply(null, deferreds).done(callback);
  }
};

/**
 * Serializes a form to JSON
 * http://stackoverflow.com/questions/1184624/convert-form-data-to-js-object-with-jquery
 */
$.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

function formAsJSON($form) {
  return JSON.stringify($form.serializeObject());
}

/**
 * Utility to get a keyed value from the aoData (for datatables)
 */
function fnGetKey( aoData, sKey ) {
  for ( var i=0, iLen=aoData.length ; i<iLen ; i++ ) {
    if ( aoData[i].name == sKey ) {
      return aoData[i].value;
    }
  }
  return null;
}          

/**
 * Add some utilities to _
 */
_.mixin({

  /**
   * https://github.com/virajsanghvi/underscore-eval-string
   * Takes a string and evaluates it in some context.
   * This is helpful for translating something like 'om.function',
   * to the actual function.
   *
   * @param {String} name String to evaluate
   * @param {Object} context Context to evaluate under (defaults to window)
   * @returns {Object} result of evaluation
   */
  evalString: function (name, context) {
    context = context || window;
    var args = Array.prototype.slice.call(arguments).splice(2);
    var namespaces = name.split(".");
    var obj = namespaces.pop();
    for(var i = 0; i < namespaces.length; i++) {
      context = context[namespaces[i]];
    }
    return context[obj];
  },
  
  /**
   * Should the context hold the key, then a DD list item is returned in the form:
   *   <dt>title</dt>
   *   <dd>context.key</dd>
   * Otherwise nothing is returned
   */
  optionalDDItem: function(context, key, title) {
    var value = _.evalString(key, context);
    if (value !== undefined) {
      return "<dt>" + title +"</dt><dd>" + value + "</dd>";
    } else {
      return "";
    }
  },

  eval: function(context, key, tmpl) {
    var val = _.evalString(key, context);
    if (val !== undefined) {
      return _.template(tmpl, {value : val}, {interpolate : /\{\{(.+?)\}\}/g});
    }
  },
});