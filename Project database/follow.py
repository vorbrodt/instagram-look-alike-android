from flask import *
from flask_sqlalchemy import SQLAlchemy
from data_base import *
from token_check import *

"""
Gets all the people some one follows
"""
@app.route("/download/following/info/<email>" , methods = ["GET"])
@jwt_required
def get_following_info(email):
    if not check_token() == True:
        return check_token()
    user = login_deatails_db.query.filter_by(email=str(email)).first()
    username = user.user_info
    user_db = User_db.query.filter_by(username=str(username[0])).first()
    json_dictionary = {"following": str(user_db.following)}
    return jsonify(json_dictionary)
"""
Uppdating that some one follow or un followed some one,
adds the follower to there following.
"""
@app.route("/follow/user" , methods = ["POST"])
@jwt_required
def follow_user():
    if not check_token() == True:
        return check_token()

    email = request.form.get('email')
    follow_user = request.form.get('follow')
    user = login_deatails_db.query.filter_by(email=str(email)).first()
    username = user.user_info

    if str(follow_user)==str(username[0]):
        return "You cannot follow yourself", 400

    #add that we are following some user (append comes later below)
    user_db = User_db.query.filter_by(username=str(username[0])).first()
    following_object = Following_db(username = str(follow_user))

    #add that a user now got a new follower
    other_user_db = User_db.query.filter_by(username=str(follow_user)).first()

    if not other_user_db:
        return "User does not exist", 400
    follower_object = Followers_db(username = str(username[0]))

    #if already follow then unfollow
    if str(username[0]) in  [str(profile) for profile in other_user_db.followers]:
        Following_db.query.filter_by(username = str(follow_user), following_id=str(user_db.id)).delete()
        Followers_db.query.filter_by(username = str(username[0]), followers_id=str(other_user_db.id)).delete()
        db.session.commit()
        return "", 200
    else:
        #otherwise follow new user
        other_user_db.followers.append(follower_object)
        #add that a user now got a new follower
        user_db.following.append(following_object)
        db.session.commit()
        return "", 200

"""
Gets the users news feed
"""
@app.route("/newsfeed/<email>" , methods = ["GET"])
@jwt_required
def newsfeed(email):
    if not check_token() == True:
        return check_token()
    user = login_deatails_db.query.filter_by(email=str(email)).first()
    username = user.user_info
    user_db = User_db.query.filter_by(username=str(username[0])).first()
    following = user_db.following

    list_of_followers = []
    for elm in following:
        username = elm.username
        user_db = User_db.query.filter_by(username=str(username)).first()
        time = user_db.time
        newst_meme = user_db.newest_meme
        if not time or not newst_meme or not username:
            continue
        else:
            list_of_followers.append({"username":str(username),"time":str(time)})

    if not list_of_followers:
        return "",400

    list_of_followers.sort(key=lambda item:item['time'], reverse=True)

    return jsonify(list_of_followers)

"""
Get the any users newst upload.
"""
@app.route("/newest/post/<username>" , methods = ["GET"])
@jwt_required
def newest_meme(username):
    if not check_token() == True:
        return check_token()
    user_db = User_db.query.filter_by(username=str(username)).first()
    if not user_db:
        return "",400
    return jsonify({"newest_meme":str(user_db.newest_meme)})


#"newest_meme":str(newst_meme)
