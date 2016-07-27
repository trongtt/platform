(function ($) {
  documentPreview = {
    settings: {
      docId: null,
      docPath: null,
      downloadUrl: null,
      openUrl: null,
      isWebContent: false,
      showComments: false,
      labels: {
        close: "Close",
        download: "Download",
        openInDocuments: "Open in Documents",
        likeActivity: "Like",
        postCommentHint: "Add your comment...",
        noComment: "No comment yet"
      },
      user: {
        username: null,
        fullname: null,
        avatarUrl: null,
        profileUrl: null
      },
      author: {
        username: null,
        fullname: null,
        avatarUrl: null,
        profileUrl: null
      },
      activity: {
        id: null,
        postTime: "",
        status: "",
        likes: 0
      },
      comments: null
    },

    init: function (docPreviewSettings) {
      this.settings = $.extend(this.settings, docPreviewSettings);

      var promises = [];

      // if we miss author information, let's fetch them
      if(this.settings.author.username != null
        && (this.settings.author.fullname == null  || this.settings.author.avatarUrl == null || this.settings.author.profileUrl == null)) {
        promises.push(this.fetchActivityAuthorInformation());
      }
      // if we miss current user information, let's fetch them
      if(this.settings.user.fullname == null  || this.settings.user.avatarUrl == null || this.settings.user.profileUrl == null) {
        promises.push(this.fetchUserInformation());
      }

      var self = this;
      // wait for all users info fetches to be complete before rendering the component
      Promise.all(promises).then(function() {
        self.createSkeleton();
        self.render();
        self.show();
        self.loadComments();
      }, function(err) {
        // error occurred
      });
    },

    fetchUserInformation: function(callback) {
      var self = this;
      return $.ajax({
        url: "/rest/v1/social/users/" + eXo.env.portal.userName
      }).done(function (data) {
        if (data.fullname != null) {
          self.settings.user.fullname = data.fullname;
        }
        if (data.avatar != null) {
          self.settings.user.avatarUrl = data.avatar;
        } else {
          self.settings.user.avatarUrl = "/eXoSkin/skin/images/system/SpaceAvtDefault.png";
        }
        self.settings.user.profileUrl = "/" + eXo.env.portal.containerName + "/" + eXo.env.portal.portalName + "/" + eXo.env.portal.userName;
      }).always(function () {
        if(typeof callback === 'function') {
          callback();
        }
      });
    },

    fetchActivityAuthorInformation: function(callback) {
      var self = this;
      return $.ajax({
        url: "/rest/v1/social/users/" + self.settings.author.username
      }).done(function (data) {
        if (data.fullname != null) {
          self.settings.author.fullname = data.fullname;
        }
        if (data.avatar != null) {
          self.settings.author.avatarUrl = data.avatar;
        } else {
          self.settings.author.avatarUrl = "/eXoSkin/skin/images/system/SpaceAvtDefault.png";
        }
        self.settings.author.profileUrl = "/" + eXo.env.portal.containerName + "/" + eXo.env.portal.portalName + "/" + self.settings.author.username;
      }).always(function () {
        if(typeof callback === 'function') {
          callback();
        }
      });
    },

    createSkeleton: function () {
      var docPreviewContainer = $("#documentPreviewContainer");

      if(docPreviewContainer.length == 0) {
        docPreviewContainer = $("<div />", {
          id: "documentPreviewContainer",
          class: "maskLayer"
        }).appendTo('body');
      }
      docPreviewContainer.hide();

      docPreviewContainer.html(' \
        <div class="uiDocumentPreview" id="uiDocumentPreview"> \
          <div class="exitWindow"> \
            <a class="uiIconClose uiIconWhite" title="' + this.settings.labels.close + '" onclick="documentPreview.hide()"></a> \
          </div> \
          <div class="uiDocumentPreviewMainWindow clearfix"> \
            <!-- doc comments --> \
            <div class="uiBox commentArea pull-right" id="$uicomponent.id"> \
              <div class="title">\
                Title \
              </div> \
              <div class="uiContentBox"> \
                <div class="highlightBox"> \
                  <div class="profile clearfix"> \
                    <a title="' + this.settings.author.fullname + '" href="' + this.settings.author.profileUrl + '" class="avatarMedium pull-left"><img alt="' + this.settings.author.fullname + '" src="' + this.settings.author.avatarUrl + '"></a> \
                    <div class="rightBlock"> \
                      <a href="' + this.settings.author.profileUrl + '">' + this.settings.author.fullname + '</a> \
                      <p class="dateTime">' + this.settings.activity.postTime + '</p> \
                      <p class="descript" title="activityStatus">' + (this.settings.activity.status != null ? this.settings.activity.status : '') + '</p> \
                    </div> \
                  </div> \
                </div> \
                <div class="actionBar clearfix "> \
                  <ul class="pull-right"> \
                    <li> \
                      <a href="#" id="previewCommentLink"> \
                        <i class="uiIconComment uiIconLightGray"></i>&nbsp;<span class="nbOfComments"></span> \
                      </a> \
                    </li> \
                    <li> \
                      <a href="javascript:void(0);" onclick="likeActivityAction" rel="tooltip" data-placement="bottom" title="' + this.settings.labels.likeActivity + '"> \
                        <i class="uiIconThumbUp uiIconLightGray"></i>&nbsp;' + this.settings.activity.likes + ' \
                      </a> \
                    </li> \
                  </ul> \
                </div> \
                <div class="comments"> \
                  <ul class="commentList"> \
                  </ul> \
                </div> \
                <div class="commentInputBox"> \
                  <a class="avatarXSmall pull-left" href="' + this.settings.user.profileUrl + '" title="' + this.settings.user.fullname + '"> \
                    <img src="' + this.settings.user.avatarUrl + '" alt="' + this.settings.user.fullname + '" /></a> \
                    <div class="commentBox"> \
                      <textarea placeholder="' + this.settings.labels.postCommentHint + '" cols="30" rows="10" id="commentTextAreaPreview" activityId="activityId" class="textarea"></textarea> \
                    </div> \
                  </div> \
              </div> \
            </div> \
            <div class="resizeButton " id="ShowHideAll"> \
              <i style="display: block;" class="uiIconMiniArrowRight uiIconWhite"></i> \
            </div> \
            <div id="documentPreviewContent"> \
            </div> \
            <!-- put vote area here --> \
            <div class="previewBtn"> \
              <div class="downloadBtn"> \
                <a href="' + this.settings.downloadUrl + '"><i class="uiIconDownload uiIconWhite"></i>&nbsp;' + this.settings.labels.download + '</a> \
              </div> \
              <div class="openBtn"> \
                <a href="' + this.settings.openUrl + '"><i class="uiIconGotoFolder uiIconWhite"></i>&nbsp;' + this.settings.labels.openInDocuments + '</a> \
              </div> \
            </div> \
          </div> \
        </div>');
    },

    loadComments: function() {
      if(this.settings.comments == null && this.settings.activity.id != null) {
        // load comments activity
        var commentsContainer = $('#documentPreviewContainer .comments');
        commentsContainer.html('<span class="uiLoadingIconSmall"></span>');

        var self = this;
        $.ajax({
          url: '/rest/v1/social/activities/' + this.settings.activity.id + '/comments?expand=identity'
        }).done(function(data) {
          self.renderComments(data.comments);
        });
      } else if(this.settings.comments != null) {
        this.renderComments(this.settings.comments);
      } else {
        this.renderComments([]);
      }
    },

    renderComments: function(comments) {
      var commentsContainer = $('#documentPreviewContainer .comments');
      var commentsHtml = '';
      if(comments != null && comments.length > 0) {
        $('#documentPreviewContainer .nbOfComments').html(comments.length);
        commentsHtml = '<ul class="commentList">';
        $.each(comments, function (index, comment) {
          var commenterProfileUrl = "/" + eXo.env.portal.containerName + "/" + eXo.env.portal.portalName + "/" + eXo.env.portal.userName;
          var commenterAvatar = comment.identity.profile.avatar;
          if (commenterAvatar == null) {
            commenterAvatar = '/eXoSkin/skin/images/system/UserAvtDefault.png';
          }
          commentsHtml += '<li class="clearfix"> \
            <a class="avatarXSmall pull-left" href="' + commenterProfileUrl + '" title="' + comment.identity.profile.fullname + '"><img src="' + commenterAvatar + '" alt="" /></a> \
            <div class="rightBlock"> \
              <div class="tit"> \
                <a href="' + commenterProfileUrl + '" >' + comment.identity.profile.fullname + '</a> \
                <span class="pull-right dateTime">' + comment.updateDate + '</span> \
              </div> \
              <p class="cont">' + comment.body + '</p> \
              <a href="javascript:void(0)" id="$idDeleteComment" data-confirm="$labelToDeleteThisComment" data-delete="<%=uicomponent.event(uicomponent.REMOVE_COMMENT, it.id); %>"  class="close previewControllDelete"><i class="uiIconLightGray uiIconClose " commentId="$it.id"></i></a> \
            </div> \
          </li>';
        })
        commentsHtml += '</ul>';
      } else {
        $('#documentPreviewContainer .nbOfComments').html('0');
        commentsHtml = '<div class="noComment"> \
            <div class="info">' + this.settings.labels.noComment + '</div> \
          </div>';
      }
      commentsContainer.html(commentsHtml);
    },

    render: function () {
      $(window).on('resize', resizeEventHandler);
      $(document).on('keyup', closeEventHandler);

      // Bind close event. Return body scroll, turn off keyup
      $(".exitWindow > .uiIconClose", $('#uiDocumentPreview')).click(function() {
        $('body').removeClass('modal-open');
        setTimeout(function() {
          $('body').css('overflow', 'visible');
          $(document).off('keyup', closeEventHandler);
          $(window).off('resize', resizeEventHandler);
        }, 500);
      });

      var docContentContainer = $('#documentPreviewContent');

      var self = this;
      docContentContainer.load('/rest/private/contentviewer/repository/collaboration/' + this.settings.docId, function() {
        resizeEventHandler();
        self.show();
      });
    },

    show: function () {
      $('#documentPreviewContainer').show();
    },

    hide: function () {
      $('#documentPreviewContainer').hide();
    }

  };

  // Bind Esc key
  var closeEventHandler = function(e) {
    $('#presentationMode').blur();
    if (e.keyCode == 27 && ("presentationMode" != e.target.id || $.browser.mozilla)) {
      $(".exitWindow > .uiIconClose", $('#uiDocumentPreview')).trigger("click");
    }
  }

  // Resize Event
  var resizeEventHandler = function() {
    // Calculate margin
    var pdfDisplayAreaHeight = window.innerHeight - 92;
    var $uiDocumentPreview = $('#uiDocumentPreview');
    $('#outerContainer', $uiDocumentPreview).height(pdfDisplayAreaHeight); // pdf viewer
    var $commentArea = $('.commentArea', $uiDocumentPreview);
    var $commentAreaTitle = $('.title', $commentArea);
    var $commentInputBox = $('.commentInputBox', $commentArea);
    var $commentList = $('.commentList', $commentArea);
    var $highlightBox = $('.highlightBox', $commentArea);
    var $actionBarCommentArea = $('.actionBar', $commentArea);
    var commentAreaHeight = window.innerHeight - 30;
    $commentArea.height(commentAreaHeight);
    $commentList.css('max-height', commentAreaHeight - $commentAreaTitle.innerHeight() - $commentInputBox.innerHeight() - $highlightBox.innerHeight() - $actionBarCommentArea.innerHeight() - 16); //16 is padding of commentList
    $commentList.scrollTop(20000);

    // Media viewer, no preview file
    var $navigationContainer = $(".navigationContainer", $uiDocumentPreview);
    var $uiContentBox = $('.uiContentBox', $navigationContainer);
    var $video = $('.videoContent', $uiContentBox);
    var $flowplayerContentDetail = $('.ContentDetail', $uiContentBox);
    var $flowplayerPlayerContent = $('.PlayerContent', $flowplayerContentDetail);
    var $flowplayer = $('object', $flowplayerPlayerContent);
    var $flashViewer = $('.FlashViewer', $uiContentBox);
    var $embedFlashViewer = $('embed', $flashViewer);
    var $windowmediaplayer = $('#MediaPlayer1', $uiContentBox);
    var $embedWMP = $('embed', $windowmediaplayer);

    $navigationContainer.height(pdfDisplayAreaHeight);
    $uiContentBox.height(pdfDisplayAreaHeight);
    $flowplayerContentDetail.height(pdfDisplayAreaHeight);
    $flowplayerPlayerContent.height(pdfDisplayAreaHeight - 5);
    $flashViewer.height(pdfDisplayAreaHeight - 5);

    $flowplayer.css('max-width', $uiContentBox.width() - 2);
    $flowplayer.css('max-height', $uiContentBox.height() - 3);
    $flowplayer.css('width', '100%');
    $flowplayer.css('height', '100%');

    $video.css('max-width', $uiContentBox.width() - 2);
    $video.css('max-height', $uiContentBox.height() - 3);
    $video.css('width', '100%');
    $video.css('height', '100%');

    $windowmediaplayer.css('max-width', $uiContentBox.width() - 2);
    $windowmediaplayer.css('max-height', $uiContentBox.height() - 7);
    $windowmediaplayer.css('width', '100%');
    $windowmediaplayer.css('height', '100%');
    $embedWMP.css('width', '100%');
    $embedWMP.css('height', '100%')

    $embedFlashViewer.css('max-width', $uiContentBox.width() - 2);
    $embedFlashViewer.css('max-height', $uiContentBox.height() - 3);
    $embedFlashViewer.css('width', '100%');
    $embedFlashViewer.css('height', '100%');

    var $img = $('a > img', $uiContentBox);

    if ($img.length > 0) {
      $img.css('max-width', $uiContentBox.width() + 1);
      $img.css('max-height', $uiContentBox.height() + 1);
      $img.css('width', 'auto');
      $img.css('height', 'auto');
      $navigationContainer.css('overflow', 'hidden');
    }

    $('.uiPreviewWebContent', $uiDocumentPreview).height(pdfDisplayAreaHeight - 30) // webcontent
    var $EmbedHtml =  $('.EmbedHtml', $uiDocumentPreview);
    $EmbedHtml.height(pdfDisplayAreaHeight) // External embedded

    // Resize image flick
    $img = $('.uiDocumentPreviewMainWindow > .EmbedHtml > a > img');
    $img.css('max-width', $EmbedHtml.width());
    $img.css('max-height', $EmbedHtml.height());
  }

  return documentPreview;
})($);