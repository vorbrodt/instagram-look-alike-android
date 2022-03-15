from flask import *
from flask_sqlalchemy import SQLAlchemy
from data_base import *
from token_check import *


"""
This method is to get a memes comments likes and dislikes.
"""
@app.route("/display/meme" , methods = ["POST"])
@jwt_required
def display_meme():
    if not check_token() == True:
        return check_token()
    meme_pic = request.form.get("memePic")
    get_meme = Upload_db.query.filter_by(meme=meme_pic).first()
    if not get_meme:
        return "", 400
    if not get_meme.likes:
        likes=0
    else:
        likes=len(get_meme.likes)
    if not get_meme.dislikes:
        dislikes=0
    else:
        dislikes=len(get_meme.dislikes)
    memeinfo_dictionary = {"memeTitle": str(get_meme.memeTitle), "likes": str(likes), "dislikes": str(dislikes),"comments":str(get_meme.comments)}
    return jsonify(memeinfo_dictionary)

"""
spesific meme gets uppvote or undo based on the conditon the user is in.
"""
@app.route("/meme/upVote" , methods = ["POST"])
@jwt_required
def upVote():
    if not check_token() == True:
        return check_token()
    meme_pic = request.form.get("memePic")
    email = request.form.get("email")
    get_meme = Upload_db.query.filter_by(meme=meme_pic).first()
    like = Like_db(email = str(email))

    if not get_meme or str(email) in [str(elem) for elem in get_meme.dislikes]:
        return "", 400

    if str(email) in [str(elem) for elem in get_meme.likes]:
        Like_db.query.filter_by(email = str(email),memes_id=str(get_meme.id)).delete()
        db.session.commit()
        return "",200


    get_meme.likes.append(like)
    db.session.commit()
    return "", 200

"""
spesific meme gets downvoted or undo based on the conditon the user is in.
"""
@app.route("/meme/downVote" , methods = ["POST"])
@jwt_required
def downVote():
    if not check_token() == True:
        return check_token()
    meme_pic = request.form.get("memePic")
    email = request.form.get("email")
    get_meme = Upload_db.query.filter_by(meme=meme_pic).first()
    dislike = Dislike_db(email = str(email))

    if not get_meme or str(email) in [str(elem) for elem in get_meme.likes]:
        return "", 400

    if str(email) in [str(elem) for elem in get_meme.dislikes]:
        Dislike_db.query.filter_by(email = str(email),memes_id=str(get_meme.id)).delete()
        db.session.commit()
        return "",200


    get_meme.dislikes.append(dislike)
    db.session.commit()
    return "", 200

"""
Post a comment on a spesific meme.
"""
@app.route("/post/comment" , methods = ["POST"])
@jwt_required
def post_comment():
    if not check_token() == True:
        return check_token()
    meme_pic = request.form.get("memePic")
    email = request.form.get("email")
    comment = request.form.get("comment")

    if not meme_pic:
        return "", 400
    get_meme = Upload_db.query.filter_by(meme=meme_pic).first()
    if not comment or not email or not get_meme:
        return "", 400
    user = login_deatails_db.query.filter_by(email=email).first()
    username = user.user_info
    if not username:
        return "", 400

    add_comment = Comment_db(username=str(username),text_comment=str(comment))
    get_meme.comments.append(add_comment)
    db.session.commit()
    return "", 200
