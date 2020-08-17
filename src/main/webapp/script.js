updateComments = ()=>{
    var commentsDiv = document.querySelector('#comments');
    commentsDiv.innerHTML =comments.map(function (comment) {
        return '<p>'+comment+'</p><hr>';
    }).join('');
}

fetch('/comment')
  .then(response => response.json())
  .then(data =>{ comments = data.map((obj)=>obj.comment); updateComments()});

commentForm.addEventListener('submit', (event) => {
    event.preventDefault();
    comment = document.getElementById("commentBox").value;
    comment = comment.trim();
    commentForm.submit();
});