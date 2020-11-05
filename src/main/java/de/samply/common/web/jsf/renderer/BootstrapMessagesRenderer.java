package de.samply.common.web.jsf.renderer;

import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.MessagesRenderer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.component.UIMessages;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

/**
 * Component for twitter bootstrap alerts. Overrides default JSF Message renderer with Bootstrap
 * alert design.
 *
 * @author vlcekmi3 (https://gist.github.com/vlcekmi3/4151211)
 */
@FacesRenderer(componentFamily = "javax.faces.Messages", rendererType = "javax.faces.Messages")
public class BootstrapMessagesRenderer extends MessagesRenderer {

  private static final Attribute[] ATTRIBUTES =
      AttributeManager.getAttributes(AttributeManager.Key.MESSAGESMESSAGES);

  @Override
  public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
    super.encodeBegin(context, component);
  }

  @Override
  public void encodeEnd(FacesContext context, UIComponent component) throws IOException {

    rendererParamsNotNull(context, component);
    if (!shouldEncode(component)) {
      return;
    }

    boolean mustRender = shouldWriteIdAttribute(component);
    UIMessages messages = (UIMessages) component;
    ResponseWriter writer = context.getResponseWriter();

    String clientId = ((UIMessages) component).getFor();
    if (clientId == null && messages.isGlobalOnly()) {
      clientId = "";
    }

    Iterator messageIt = getMessageIter(context, clientId, component);
    if (!messageIt.hasNext()) {
      if (mustRender) {
        if ("javax_faces_developmentstage_messages".equals(component.getId())) {
          return;
        }
        writer.startElement("div", component);
        writeIdAttributeIfNecessary(context, writer, component);
        writer.endElement("div");
      }
      return;
    }

    writeIdAttributeIfNecessary(context, writer, component);
    RenderKitUtils.renderPassThruAttributes(context, writer, component, ATTRIBUTES);

    Map<Severity, List<FacesMessage>> msgs = new HashMap<>();
    msgs.put(FacesMessage.SEVERITY_INFO, new ArrayList<FacesMessage>());
    msgs.put(FacesMessage.SEVERITY_WARN, new ArrayList<FacesMessage>());
    msgs.put(FacesMessage.SEVERITY_ERROR, new ArrayList<FacesMessage>());
    msgs.put(FacesMessage.SEVERITY_FATAL, new ArrayList<FacesMessage>());

    while (messageIt.hasNext()) {
      FacesMessage curMessage = (FacesMessage) messageIt.next();
      if (curMessage.isRendered() && !messages.isRedisplay()) {
        continue;
      }
      msgs.get(curMessage.getSeverity()).add(curMessage);
    }

    List<FacesMessage> severityMessages = msgs.get(FacesMessage.SEVERITY_FATAL);
    if (!severityMessages.isEmpty()) {
      encodeSeverityMessages(context, messages, FacesMessage.SEVERITY_FATAL, severityMessages);
    }

    severityMessages = msgs.get(FacesMessage.SEVERITY_ERROR);
    if (!severityMessages.isEmpty()) {
      encodeSeverityMessages(context, messages, FacesMessage.SEVERITY_ERROR, severityMessages);
    }

    severityMessages = msgs.get(FacesMessage.SEVERITY_WARN);
    if (!severityMessages.isEmpty()) {
      encodeSeverityMessages(context, messages, FacesMessage.SEVERITY_WARN, severityMessages);
    }

    severityMessages = msgs.get(FacesMessage.SEVERITY_INFO);
    if (!severityMessages.isEmpty()) {
      encodeSeverityMessages(context, messages, FacesMessage.SEVERITY_INFO, severityMessages);
    }
  }

  private void encodeSeverityMessages(FacesContext facesContext, UIMessages uiMessages,
      Severity severity, List<FacesMessage> messages) throws IOException {
    ResponseWriter writer = facesContext.getResponseWriter();
    String alertSeverityClass = "";
    String alertIconClass = "";

    if (FacesMessage.SEVERITY_INFO.equals(severity)) {
      alertSeverityClass = "alert-info";
      alertIconClass = "fa fa-lg fa-info-circle";
    } else if (FacesMessage.SEVERITY_WARN.equals(severity)) {
      alertSeverityClass = "alert-warning";
      alertIconClass = "fa fa-lg fa-exclamation-circle";
    } else if (FacesMessage.SEVERITY_ERROR.equals(severity)) {
      alertSeverityClass = "alert-danger";
      alertIconClass = "fa fa-lg fa-times-circle";
    } else if (FacesMessage.SEVERITY_FATAL.equals(severity)) {
      alertSeverityClass = "alert-danger";
      alertIconClass = "fa fa-lg fa-times-circle";
    }

    writer.startElement("div", null);
    writer.writeAttribute("class", "alert " + alertSeverityClass, "alert " + alertSeverityClass);
    writer.writeAttribute("role", "alert", "alert");
    writer.startElement("a", uiMessages);
    writer.writeAttribute("class", "close", "class");
    writer.writeAttribute("data-dismiss", "alert", "data-dismiss");
    writer.writeAttribute("href", "#", "href");
    writer.write("&#xd7;");
    writer.endElement("a");

    writer.startElement("ul", null);
    writer.writeAttribute("style", "list-style: none;", "list-style: none;");

    for (FacesMessage msg : messages) {
      String summary = msg.getSummary() != null ? msg.getSummary() : "";
      String detail = msg.getDetail() != null ? msg.getDetail() : summary;

      writer.startElement("li", uiMessages);

      writer.startElement("i", null);
      writer.writeAttribute("class",
          alertIconClass, alertIconClass);
      writer.writeAttribute("style",
          "margin-right: 5px; margin-top: 5px; float:left;",
          "margin-right: 5px; margin-top: 5px; float:left;");
      writer.endElement("i");
      writer.startElement("div", null);
      if (uiMessages.isShowSummary()) {
        writer.startElement("span", uiMessages);
        writer.writeAttribute("style",
            "font-size: large; font-weight: bold;", "font-size: large; font-weight: bold;");
        writer.writeText(summary, uiMessages, null);
        writer.endElement("span");
      }

      if (uiMessages.isShowDetail()) {
        writer.writeText(" " + detail, null);
      }
      writer.endElement("div");
      writer.endElement("li");
      msg.rendered();
    }
    writer.endElement("ul");
    writer.endElement("div");
  }
}
