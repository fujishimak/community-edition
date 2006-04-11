package org.alfresco.web.bean.generator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.web.app.servlet.FacesHelper;
import org.alfresco.web.ui.common.component.UIListItems;
import org.alfresco.web.ui.repo.RepoConstants;
import org.alfresco.web.ui.repo.component.property.PropertySheetItem;
import org.alfresco.web.ui.repo.component.property.UIPropertySheet;

/**
 * Generates the image picker component with rounded corners for selecting
 * an icon for a space.
 * 
 * @author gavinc
 */
public class SpaceIconPickerGenerator extends BaseComponentGenerator
{
   public UIComponent generate(FacesContext context, String id)
   {
      // create the outer component
      UIComponent component = context.getApplication().
            createComponent(RepoConstants.ALFRESCO_FACES_IMAGE_PICKER);
      
      // setup the outer component
      component.setRendererType(RepoConstants.ALFRESCO_FACES_RADIO_PANEL_RENDERER);
      FacesHelper.setupComponentId(context, component, id);
      component.getAttributes().put("columns", new Integer(6));
      component.getAttributes().put("spacing", new Integer(4));
      component.getAttributes().put("panelBorder", "blue");
      component.getAttributes().put("panelBgcolor", "#D3E6FE");
      
      return component;
   }
   
   public UIComponent generate(FacesContext context, UIPropertySheet propertySheet,
         PropertySheetItem item)
   {
      UIComponent component = null;
      
      // get the property definition
      PropertyDefinition propertyDef = getPropertyDefinition(context,
            propertySheet.getNode(), item.getName());
         
      if (propertySheet.inEditMode())
      {
         // use the standard component in edit mode
         component = generate(context, item.getName());
      
         // create the list items child component
         UIListItems items = (UIListItems)context.getApplication().
               createComponent(RepoConstants.ALFRESCO_FACES_LIST_ITEMS);
         
         // setup the value binding for the list of icons, this needs
         // to be sensitive to the bean used for the property sheet
         // we therefore need to get the value binding expression and
         // extract the bean name and then add '.icons' to the end,
         // this means any page that uses this component must supply
         // a getIcons method that returns a List of UIListItem's
         ValueBinding binding = propertySheet.getValueBinding("value");
         String expression = binding.getExpressionString();
         String beanName = expression.substring(2, expression.indexOf(".")+1);
         if (beanName.equals("DialogManager") || beanName.equals("WizardManager"))
         {
            // deal with the special dialog and wizard manager beans by 
            // adding .bean
            beanName = beanName + "bean.";
         }
         String newExpression = "#{" + beanName + "icons}";
         
         ValueBinding vb = context.getApplication().createValueBinding(newExpression);
         items.setValueBinding("value", vb);
         
         // add the list items component to the image picker component
         component.getChildren().add(items);
         
         // disable the component if it is read only or protected
         if (item.isReadOnly() || (propertyDef != null && propertyDef.isProtected()))
         {
            component.getAttributes().put("disabled", Boolean.TRUE);
         }
         else
         {
            // if the item is multi valued we need to wrap the standard component
            if (propertyDef != null && propertyDef.isMultiValued())
            {
               component = enableForMultiValue(context, propertySheet, item, component, true);
            }
         }
      }
      else
      {
         // create an output text component in view mode
         component = createOutputTextComponent(context, item.getName());
         
         // if the property is multi-valued and there isn't a custom converter 
         // specified, add the MultiValue converter as a default
         if (propertyDef.isMultiValued() && item.getConverter() == null)
         {
            item.setConverter(RepoConstants.ALFRESCO_FACES_MULTIVALUE_CONVERTER);
         }
      }
      
      // setup the converter if one was specified
      setupConverter(context, propertySheet, item, component);
      
      return component;
   }
}
