window.Router = Backbone.Router.extend({
    routes: {
        "": "home",
        "home": "home",
        "node-search": "nodeSearch",
        "node-create": "nodeCreate",
        "node-detail/:key": "nodeDetail",
        "organization-search": "organizationSearch",
        "dataset-search": "datasetSearch",
        "installation-search": "installationSearch",
        "pending-endorsements": "pendingEndorsements"
        
    },

    initialize: function () {
        this.headerView = new HeaderView();
        $('.header').html(this.headerView.render().el);
    },

    home: function () {
      this.homeview = new HomeView();
      this.homeview.render();
      $("#content").html(this.homeview.el);      
      $('#tabs').find('a[href$="#home"]').tab('show'); // required for refresh
    },
    
    nodeSearch: function () {
      this.nodesearch = new NodeSearchView();
      this.nodesearch.render();
      $("#content").html(this.nodesearch.el);
      $('#tabs').find('a[href$="#node-search"]').tab('show'); // required for refresh
    },
    
    nodeCreate: function () {
      this.view = new NodeCreateView({model: new NodeModel()});
      this.view.render();
      $("#content").html(this.view.el);
      $('#tabs').find('a[href$="#node-search"]').tab('show'); // required for refresh
    },

    nodeDetail: function (key) {
			// defer rendering until the response occurs
      var node = new NodeModel({key: key}); 
      node.fetch( {
        success: function (data) {
          console.log(data);
          $("#content").html(new NodeDetailView({model: data}).render().el);
          $('#tabs').find('a[href$="#node-search"]').tab('show'); // required for refresh
        }        
      });
    },
    
    organizationSearch: function () {
      this.organizationsearch = new OrganizationSearchView();
      this.organizationsearch.render();
      $("#content").html(this.organizationsearch.el);
      $('#tabs').find('a[href$="#organization-search"]').tab('show'); // required for refresh
    },

    datasetSearch: function () {
      this.datasetsearch = new DatasetSearchView();
      this.datasetsearch.render();
      $("#content").html(this.datasetsearch.el);
      $('#tabs').find('a[href$="#dataset-search"]').tab('show'); // required for refresh
    },

    installationSearch: function () {
      this.installationsearch = new InstallationSearchView();
      this.installationsearch.render();
      $("#content").html(this.installationsearch.el);
      $('#tabs').find('a[href$="#installation-search"]').tab('show'); // required for refresh
    },
    
    pendingEndorsements: function () {
      $('#tabs').find('a[href$="#more"]').tab('show'); // required for refresh
    },
});

// Start up the template loader which maps views to templates by naming convention 
templateLoader.load(
  ["HomeView", "HeaderView", 
  "NodeSearchView", "NodeCreateView", "NodeDetailView",
  "TagCollectionView", "TagView",
  "ContactCollectionView", "ContactView",
  //"OrganizationSearchView", "DatasetSearchView", "InstallationSearchView",
  ],
  function () {
    app = new Router();
    Backbone.history.start();
});

