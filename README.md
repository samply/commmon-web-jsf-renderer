UNMERGED BRANCH -CHANGES FROM OTHER SAMPLY TEAMS MISSING
# JSF Bootstrap Message and Messages Renderer


Style the h:messages component from JSF with [bootstrap alerts](https://getbootstrap.com/docs/3.3/components/#alerts)


Use maven to build the jar:

```
mvn clean install
```

In order to use it in any JSF2 project:

* Bootstrap has to be included in the project
* Add the following snippet to _faces-config.xml_:

   ```xml
   <!-- Register the custom renderers to use bootstrap message(s) -->
   <render-kit>
       <renderer>
           <component-family>javax.faces.Message</component-family>
           <renderer-type>javax.faces.Message</renderer-type>
           <renderer-class>de.samply.share.client.renderer.BootstrapMessageRenderer</renderer-class>
       </renderer>
       <renderer>
           <component-family>javax.faces.Messages</component-family>
           <renderer-type>javax.faces.Messages</renderer-type>
           <renderer-class>de.samply.share.client.renderer.BootstrapMessagesRenderer</renderer-class>
       </renderer>
   </render-kit>
 ```
