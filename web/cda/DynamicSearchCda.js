/**
                                        `.------:::--...``.`                                        
                                    `-:+hmmoo+++dNNmo-.``/dh+...                                    
                                   .+/+mNmyo++/+hmmdo-.``.odmo -/`                                  
                                 `-//+ooooo++///////:---..``.````-``                                
                           `````.----:::/::::::::::::--------.....--..`````                         
           ```````````...............---:::-----::::---..------------------........```````          
        `:/+ooooooosssssssyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyysssssssssssssssssssssssssssoo+/:`       
          ``..-:/++ossyhhddddddddmmmmmarea51mbobmlazarmmmmmmmddddddddddddddhhyysoo+//:-..``         
                      ```..--:/+oyhddddmmmmmmmmmmmmmmmmmmmmmmmddddys+/::-..````                     
                                 ``.:oshddmmmmmNNNNNNNNNNNmmmhs+:.`                                 
                                       `.-/+oossssyysssoo+/-.`                                      
                                                                                                     
    
*/

$.widget("magic.DynamicSearchCda", {

    _toggleSelect: function($selectable){
        if ($selectable.hasClass("selectable-default")){
            this._setSelected($selectable);
        } else if ($selectable.hasClass("selectable-selected")) {
            this._setNotSelected($selectable);
        }
    },

    _setSelected: function($selectable){
        $selectable.removeClass("selectable-default");
        $selectable.addClass("selectable-selected");
        $selectable.attr("data-selected", "selected");
    },

    _setNotSelected: function($selectable){
        $selectable.removeClass("selectable-selected");
        $selectable.addClass("selectable-default");
        $selectable.attr("data-selected", "none");
    },

    _create: async function(){
        let self = this;
        let ops = this.options;

        self.pagingRequestFilter = ops.pagingRequestFilter;
        self._display();
    },
    
    _display: async function(){
        let self = this;

        let pagingRequestFilter = self.pagingRequestFilter;
        let preFilteredTags = await ajaxCda({action: "preFilterTags", data: JSON.stringify(pagingRequestFilter)});

        let hDynamicSearchCdaTemplate = Handlebars.compile(dynamicSearchCdaTemplate);
        self.element.html(hDynamicSearchCdaTemplate({tags: preFilteredTags}));

        let $articlesSelectable = self.element.find('[data-role="post-type"][data-id="articles"]');
        let $photosSelectable = self.element.find('[data-role="post-type"][data-id="photos"]');
        let $galleriesSelectable = self.element.find('[data-role="post-type"][data-id="galleries"]');

        if (pagingRequestFilter.needArticles){
            self._setSelected($articlesSelectable);
        }

        if (pagingRequestFilter.needPhotos){
            self._setSelected($photosSelectable);
        }

        if (pagingRequestFilter.needGalleries){
            self._setSelected($galleriesSelectable);
        }

        if (pagingRequestFilter.tags){
            $.each(pagingRequestFilter.tags, function(index, tag){
                let $tagSelectable = self.element.find('[data-role="post-tag"][data-id="' + tag + '"]');
                self._setSelected($tagSelectable);
            });
        }

        let $typesSelectables = self.element.find('[data-role="post-type"]');
        let $tagsSelectables = self.element.find('[data-role="post-tag"]');

        $typesSelectables.unbind();
        $typesSelectables.click(function(){
            self._toggleSelect($(this));
            self.enrichFilter();
            self._display();
        });

        $tagsSelectables.unbind();
        $tagsSelectables.click(function(){
            self._toggleSelect($(this));
            self.enrichFilter();
            self._display();
        });

    },
    enrichFilter: function(){
        let self = this;
        
        let tags = [];
        let nothingSelected = true;

        let pagingRequestFilter = self.pagingRequestFilter;

        pagingRequestFilter.needArticles = false;
        pagingRequestFilter.needPhotos = false;
        pagingRequestFilter.needGalleries = false;

        let $postTypes = self.element.find('[data-role="post-type"][data-selected="selected"]');
        $.each($postTypes, function(index, value){
            if ($(value).data("id") === "articles"){
                pagingRequestFilter.needArticles = true;
                nothingSelected = false;
            }
            if ($(value).data("id") === "photos"){
                pagingRequestFilter.needPhotos = true;
                nothingSelected = false;
            }
            if ($(value).data("id") === "galleries"){
                pagingRequestFilter.needGalleries = true;
                nothingSelected = false;
            }
        });

        if (nothingSelected){
            return null;
        }

        let $tags = self.element.find('[data-role="post-tag"][data-selected="selected"]');
        $.each($tags, function(index, value){
            tags.push($(value).data("id"));
        });

        pagingRequestFilter.tags = tags;
    }
});