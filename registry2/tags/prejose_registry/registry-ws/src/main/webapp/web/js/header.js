window.HeaderView = Backbone.View.extend({

    render: function () {
        $(this.el).html(this.template());
        return this;
    },
    
    events: {
        "click .nav-tabs a": "navigateTab",
    },

    navigateTab: function(e) {
      app.navigate(e.currentTarget.hash, {trigger: true}); 
      // bubble up the change to fire the backbone routing
      //window.location.hash = e.currentTarget.hash;
    }
});