window.TagModel = Backbone.Model.extend({
  idAttribute  : "key", // the GBIF field is key
  
  // custom sync functionality to not post JSON (special service taking only a String)
  sync : function(method, model, options) {
    var httpMethod = "POST";
    if (method == "delete") {
      httpMethod = "DELETE";
    }
    $.ajax({
      url: this.url(),
      type: httpMethod,
      data: model.get("value"),
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      success: function(data) {
        model.set({"key" : data});
      }
    });        
  }
});

window.TagsModel = Backbone.Collection.extend({
  model : TagModel,
  initialize : function(props) {
    this.url = props.url; 
  },
});

window.TagView = Backbone.View.extend({
  events:{
    'click span.close': 'deleteTag',
  },

  render: function () {
    $(this.el).html(this.template(this.model.toJSON()));
    return this;
  },
  
  deleteTag : function (e) {
    e.preventDefault();
    this.model.destroy();
  },
});

window.TagCollectionView = Backbone.View.extend({
  initialize:function () {
    this.model.bind('change', this.renderTag, this); 
    this.model.bind('remove', this.removeTag, this);
  },
  
  events:{
    "submit form.tag": "addTag",
  },
  
  render: function () {
    $(this.el).html(this.template(this.model.toJSON()));    
    _.each(this.model.models, function (tag) {
      this.renderTag(tag)
    }, this);
    return this;
  },
  
  addTag : function (e) {
    e.preventDefault();
    this.model.create(new TagModel({"value":$("#tagInput").val()}));
    $("#tagInput").val("");
  },
  
  renderTag : function (tag) {
     $("div#tagContainer", this.el).append(new TagView({model:tag}).render().el);
  },
  
  removeTag : function (tag) {
     $("div[data-tagkey='" + tag.get("key") + "']", this.el).remove();     
  }
});

