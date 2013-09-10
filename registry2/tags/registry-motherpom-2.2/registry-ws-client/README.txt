Registry v2 WS Client 
---------------------

This project provides the WS client to the registry (v2) web services. It implements the GBIF Registry v2 API.

The registry-ws client can be configured in the following ways:

     i)  in read-only mode, or
     ii) read-write mode, controlled by the GbifApplicationAuthModule

i) Read only usage is demonstrated with:

      Properties props = new Properties();
      props.setProperty("registry.ws.url", "http://jawa.gbif.org:8080/registry-ws");
      Injector webserviceClient = Guice.createInjector(new RegistryWsClientModule(props), new AnonymousAuthModule());
      DatasetService ds = injector.getInstance(DatasetService.class);

ii) Read write usage is demonstrated with:

      Properties props = new Properties();
      props.setProperty("registry.ws.url", "http://jawa.gbif.org:8080/registry-ws");
      props.setProperty("application.key", "gbif.registry-ws-client-it");
      props.setProperty("application.secret", "6a55ca16c053e269a9602c02922b30ce49c49be3a68bb2d8908b24d7c1");
      // Create authentication module, and set principal name, equal to a GBIF User unique account name
      GbifApplicationAuthModule auth = new GbifApplicationAuthModule(props);
      auth.setPrincipal("admin");
      Injector webserviceClient = Guice.createInjector(new RegistryWsClientModule(props), auth);
      DatasetService ds = injector.getInstance(DatasetService.class);