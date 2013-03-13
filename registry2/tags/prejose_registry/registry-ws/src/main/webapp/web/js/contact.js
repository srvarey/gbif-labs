window.ContactModel = Backbone.Model.extend({
  idAttribute  : "key", // the GBIF field is key
  
	/**
	 * Using the provided form, saves the model and updates with the key should it be 
	 * a new entity, delegating back to the provided handlers for further action.
	 */
	update: function ($form, success, error) {
	  this.save($form.serializeObject(), {
      success: function (model, response, options) {
        // inform backbone of the id
        if (model.isNew() ) {
          model.set({"key" : response});
        }
        success(model);
      }, error: function () {
        error();
      }
    });
	},
});

window.ContactsModel = Backbone.Collection.extend({
  model : ContactModel,
  initialize : function(props) {
    this.url = props.url; 
  },
});

window.ContactView = Backbone.View.extend({
  render: function () {
    $(this.el).html(this.template(this.model.toJSON()));
    return this;
  },
});

window.ContactCollectionView = Backbone.View.extend({
  render: function () {
    $(this.el).html(this.template(this.model.toJSON()));    
    _.each(this.model.models, function (contact) {
      this.renderContact(contact)
    }, this);
    return this;
  },
  
  renderContact : function (contact) {
     $("div#contactContainer", this.el).append(new ContactView({model:contact}).render().el);
  },
});