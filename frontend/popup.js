// Constants
const BASE_URL = "https://summer20-sps-90.df.r.appspot.com";

// HTML components

const commentForm = `
<form id="commentForm">
    <textarea
    type="text"
    id="commentBox"
    name="commentText"
    placeholder="Tell Panda Something about this website"
    ></textarea>
    <img src="images/paw.png" id="submitButton" />
</form>`;

const getLoginButton = (loginUrl) => `
<div id="loginButton">
    <a href=${loginUrl} target="_blank" id="loginText">Login</a>
</div>`;

const getCommentCard = ({
  id,
  userId = "",
  createdAt = "",
  comment = "",
  likes = 0,
}) => {
  createdAt = new Date(createdAt);
  let time =
    createdAt.getDate() +
    "/" +
    (createdAt.getMonth() + 1) +
    "/" +
    createdAt.getFullYear() +
    " " +
    createdAt.getHours() +
    ":" +
    createdAt.getMinutes();
  return `<div class="card">
    <div class="cardHeader">
      <p class="username">${userId}</p>
      <p class="date">${time}</p>
    </div>
    <div class="cardContent">
      <p class="cardText">${comment
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")}</p>
    </div>
    <div class="cardFooter">
      <img src="images/likeButton.png" id="${
        "likeButton" + id
      }" class="likeButton" />
      <p class="likesText" id="${"likeCount" + id}">${likes} likes</p>
    </div>
  </div>`;
};

// State
var selectedFilter = "mostRecent";
var comments = [];

// state updating functions

function fetchComments(type) {
  let websiteURL = "";
  chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
    let tab = tabs[0];
    let url = new URL(tab.url);
    websiteURL = url.hostname;
    document.getElementById("mostRecentDiv").style.borderWidth = 0;
    document.getElementById("mostLikedDiv").style.borderWidth = 0;
    switch (type) {
      case "mostRecent":
        selectedFilter = "mostRecent";
        fetch(BASE_URL + "/comment?type=recent" + "&websiteURL=" + websiteURL)
          .then((response) => response.json())
          .then((data) => {
            comments = data;
            updateComments();
          });
        document.getElementById("mostRecentDiv").style.borderWidth = "2px";
        break;
      case "mostLiked":
        selectedFilter = "mostLiked";
        fetch(BASE_URL + "/comment?type=likes" + "&websiteURL=" + websiteURL)
          .then((response) => response.json())
          .then((data) => {
            comments = data;
            updateComments();
          });
        document.getElementById("mostLikedDiv").style.borderWidth = "2px";
        break;
    }
  });
}

function updateComments() {
  var commentsDiv = document.querySelector("#content");
  commentsDiv.innerHTML = comments
    .map((obj) => {
      return getCommentCard(obj);
    })
    .join("");
  comments.forEach((obj) => {
    document
      .getElementById("likeButton" + obj.id)
      .addEventListener("click", (e) => {
        updateLikes(obj.id);
      });
  });
}

function updateLikes(id) {
  console.log(id);
  fetch(BASE_URL + "/likes?postId=" + id, {
    method: "POST",
  })
    .then((response) => response.text())
    .then((data) => {
      if (data !== "")
        document.getElementById("likeCount" + id).innerHTML = data + " likes";
    });
}

function setFooter() {
  fetch(BASE_URL + "/authentication")
    .then((response) => response.json())
    .then((data) => {
      if (data.LoginStatus) {
        document.getElementById("footer").innerHTML = commentForm;
        document
          .getElementById("submitButton")
          .addEventListener("click", (e) => {
            e.preventDefault();
            let websiteURL = "";
            chrome.tabs.query({ active: true, currentWindow: true }, function (
              tabs
            ) {
              let tab = tabs[0];
              let url = new URL(tab.url);
              let websiteURL = url.hostname;
              $.ajax({
                type: "POST",
                url: BASE_URL + "/comment",
                data: { comment: $("#commentBox")[0].value, websiteURL },
              });
              $("#commentForm")[0].reset();
            });
            return false;
          });
      } else {
        document.getElementById("footer").innerHTML = getLoginButton(
          data.loginUrl
        );
      }
    });
}

// initialise the extension

fetchComments(selectedFilter);
setFooter();

// form submit and onClicks

document.getElementById("mostRecentDiv").addEventListener("click", () => {
  fetchComments("mostRecent");
});

document.getElementById("mostLikedDiv").addEventListener("click", () => {
  fetchComments("mostLiked");
});
