<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/blogpost.lib.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/modules/blog/comments.lib.js">

function main()
{
   model.showComponent = false;
   
   // gather all required data
   var site = page.url.templateArgs.site;
   var container = getTemplateParam("container", "blog");
   var path = getTemplateParam("path", "");
   var postId = getPageUrlParam("postId", null);
   
   // check whether we already loaded the item, load it otherwise
   var item = context.properties["blog-post-item"];
   if (item == undefined)
   {
      var data = fetchPost(site, container, postId);
      if (status.getCode() != status.STATUS_OK)
      {
         return;
      }
      context.properties["blog-post-item"] = data.item;
      item = data.item;
   }
   model.post = item;
   
   // don't allow adding comments to a draft post
   if (model.post.isDraft)
   {
      return;
   }
   
   // fetch the replies
   var commentsdata = fetchComments(item.nodeRef);
   if (status.getCode() != status.STATUS_OK)
   {
      return;
   }
   model.comments = commentsdata.items;
   model.showComponent = true;
}

main();
