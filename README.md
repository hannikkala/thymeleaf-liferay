Liferay addon for Thymeleaf template engine
===========================================

This small library brings Liferay URL utilities to [Thymeleaf](http://www.thymeleaf.org). It has following tags:

liferay:src
-----------

This tag modifies src-attribute.

liferay:href
------------

This tag modifies href-attribute.

liferay:value
-------------

This tag modifies value-attribute.

liferay:with
------------

This tag defines local variable to use in multiple places.

Tag Reference
-------------

Following names are reserved for internal use:

plid - Portlet Layout ID

portletname - Name of the portlet where request will be sent. For example portlet_WAR_package

lifecycle - Defines portlet lifecycle to be used. Allowed values are javax.portlet.PortletRequest constants:

    - ACTION_PHASE
    - EVENT_PHASE
    - RENDER_PHASE
    - RESOURCE_PHASE

windowState - Which window state will the URL point to: normal, exclusive, maximized

portletMode - Mode of the portlet from javax.portlet.PortletMode constants:

    - EDIT
    - HELP
    - VIEW

and also com.liferay.portal.kernel.portlet.LiferayPortletMode constants.

var - Used only with liferay:with -attribute. Defines local variable where generated URL is stored.

Every other parameter given will be sent to portlet.

Usage
-----

Example 1. link

```html
<a href="#" liferay:href="var1='value', var2=${definedVariable}, lifecycle='ACTION_PHASE'">
    Link
</a>
```

var1 and var2 are parameters to be sent to action phase of current portlet. Lifecycle defines that the request will be action request.

Example 2. with

```html
<p liferay:with="var='myurl', windowState='maximized', portletMode='edit'">
    <a href="${myurl}">Open edit in maximized window</a>
</p>
```

Example 3. action in Portlet 2.0

```html
<a href="#" liferay:href="action='find', var1=${definedVariable}">Link</a>
```

Above is only simplified form of:

```html
<a href="#" liferay:href="javax.portlet.action='find', var1=${definedVariable}, lifecycle='ACTION_PHASE'">Link</a>
```
