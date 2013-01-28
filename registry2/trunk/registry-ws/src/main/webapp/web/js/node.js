


/**
 * The RESTfully backed not search model.
 */
window.NodeModel = Backbone.Model.extend({
  urlRoot: '/node/',
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

/**
 * A view for rendering the node create form, and redirecting to the 
 * node detail view on success
 */
window.NodeCreateView = Backbone.View.extend({
  events:{
    "submit form": "createNode",
  },

  createNode: function (e) {
    e.preventDefault(); 
    this.model.update($("form"), 
      function(model) { 
        app.navigate("node-detail/" + model.id, {trigger: true}); // redirect on success
      }, 
      function() { 
        alert("error"); // todo: show the errors on the form
      }
    );
  },
  
  render: function () {
    $(this.el).html(this.template(this.model.toJSON()));
    return this;
  },
});

/**
 * The node detail view, including sub components.
 */
window.NodeDetailView = Backbone.View.extend({  
  initialize : function () {
    _.bindAll(this, "render");
    this.model.bind('change', this.render);
  },
  
  events:{
    "submit form.editNode": "editNode",
  },

  editNode: function (e) {
    e.preventDefault(); // no page reload
    this.model.save($("form.editNode").serializeObject());
    $("div.modal-backdrop").click(); // close the modal
  },


  render: function () {
  
  
    $(this.el).html(this.template(this.model.toJSON())); // the scaffolding
    
    // add the tags
    var tags = new TagsModel({url: "/node/" + this.model.id + "/tag"}); 
    tags.fetch( {
      success: function (data) {
        $("div.tags").html(new TagCollectionView({model: data}).render().el);
      }        
    });
    
    // add the contacts
    var tags = new ContactsModel({url: "/node/" + this.model.id + "/contact"}); 
    tags.fetch( {
      success: function (data) {
        $("div.contacts").html(new ContactCollectionView({model: data}).render().el);
      }        
    });
    
    return this;
  },
});

/**
 * The node search view.  This does NOT use backbone models for the table data, but rather the raw datatables
 * JQuery plugin.  This is by careful design choice, since there is no CRUD on the content, and there are no
 * backbone grids as rich as the datatables jquery plugin.
 */
window.NodeSearchView = Backbone.View.extend({

  render : function () {
    $(this.el).html(this.template());
    // defer until dom manipulation is actually complete
    _.defer(function(caller) {
      caller.populateTable();  
    }, this);    
    return this; 
  },
  
  populateTable : function () {
		var oTable = $('table#searchResults')
			.dataTable( {
				"sDom": "rt<'row'<'span6'i><'span6'p>>",
				"bServerSide": true,
				"sAjaxSource": "/node",
				"sAjaxDataProp": "results",  // the field name - e.g. {"results":[{...}]}
        "fnServerData": function ( sSource, aoData, fnCallback, oSettings ) {        
          // intercept the AJAX marshalling to manipulate the params for paging
          var echo = fnGetKey(aoData, "sEcho"); // a mandatory echo we need to return
          var limit = fnGetKey(aoData, "iDisplayLength");
          var offset = fnGetKey(aoData, "iDisplayStart");
          // simplify the HTTP params, by rewritting aodata
          // Note: we could have appended using the following
          // aoData.push( {"name": "offset", "value": offset} );
          aoData =  [
            {"name": "offset", "value": offset},
            {"name": "limit", "value": limit}
          ]; 
          oSettings.jqXHR = $.ajax( {
            "dataType": 'json',
            "type": "GET",
            "url": sSource,
            "data": aoData,
            "success": function(json) {
              // massage response for datatable paging
              $.extend(json, { 
                "iTotalRecords": json.count, 
                "iTotalDisplayRecords": json.count,
                "sEcho": echo   // the mandatory echo
              });
              fnCallback(json);
            }
          });
        },
				"aoColumns": [
					{ "mData": "title", "bSortable": false},
					{ "mData": "country", "bSortable": false, sDefaultContent: ""},
					{ "mData": "description", "bSortable": false},					
					{ "mData": "created", "bSortable": false},
					{ "mData": "modified", "bSortable": false},
					{ "mData": "deleted", "bSortable": false, sDefaultContent: ""},
				], 
				"fnRowCallback" :  function(nRow, aData, iDisplayIndex) { 
					// on click we go to the detail view
					$(nRow).bind('click', function() {
					  window.location.href="#node-detail/" + aData.key;
					});
				},
				"bProcessing": true, // Show processing indicator when busy
				"bDeferRender": true, // delay rendering for performance				
				"sPaginationType": "bootstrap"
		});  
  },
});