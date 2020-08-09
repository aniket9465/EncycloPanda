// Constants
const BASE_URL = "http://localhost:3000";

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
  username = "",
  time = "",
  commentText = "",
  likeCount = 0,
}) => {
  return `<div class="card">
    <div class="cardHeader">
      <p class="username">${username}</p>
      <p class="date">${time}</p>
    </div>
    <div class="cardContent">
      <p class="cardText">${commentText
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")}</p>
    </div>
    <div class="cardFooter">
      <img src="images/likeButton.png" class="likeButton" onclick="updateLikes(${id})" />
      <p class="likesText" id="${"likeCount" + id}">${likeCount} likes</p>
    </div>
  </div>`;
};

// State
var selectedFilter = "mostRecent";
var comments = [];

// state updating functions

function fetchComments(type) {
  document.getElementById("mostRecentDiv").style.borderWidth = 0;
  document.getElementById("mostLikedDiv").style.borderWidth = 0;
  switch (type) {
    case "mostRecent":
      selectedFilter = "mostRecent";
      fetch(BASE_URL + "/comments/?filter=mostRecent")
        .then((response) => response.json())
        .then((data) => {
          comments = data;
          updateComments();
        });
      document.getElementById("mostRecentDiv").style.borderWidth = "2px";
      break;
    case "mostLiked":
      selectedFilter = "mostLiked";
      fetch(BASE_URL + "/comments/?filter=mostLiked")
        .then((response) => response.json())
        .then((data) => {
          comments = data;
          updateComments();
        });
      document.getElementById("mostLikedDiv").style.borderWidth = "2px";
      break;
  }
}

function updateComments() {
  var commentsDiv = document.querySelector("#content");
  commentsDiv.innerHTML = comments
    .map((obj) => {
      return getCommentCard(obj);
    })
    .join("");
}

function updateLikes(id) {
  fetch(BASE_URL + "/like/?id=" + id)
    .then((response) => response.json())
    .then((data) => {
      console.log(document.getElementById("likeCount" + id)); // set its inner html to new number of likes
    });
}

function setFooter() {
  fetch(BASE_URL + "/auth")
    .then((response) => response.json())
    .then((data) => {
      if (data.isLoggedIn) {
        document.getElementById("footer").innerHTML = commentForm;
        document
          .getElementById("submitButton")
          .addEventListener("click", (e) => {
            e.preventDefault();
            $.ajax({
              type: "POST",
              url: BASE_URL + "/comment",
              data: $("#commentForm").serialize(),
            });
            $("#commentForm")[0].reset();
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
