Independent Web Actuator Sample
=================================

In this sample project we are exploring how/if it is possible to 
create an 'independent' Spring Application Context to expose
actuators on a separate port.

I.e. the actuators are exposed as web endpoints, but they are
not affected by or affecting the actuator configuration of the
main application.

In this sample the 'main' application is a regular Spribg Boot
Web App (with Embedded Tomcat Container).

The independent application is defined in 'com.example.subapp'.

