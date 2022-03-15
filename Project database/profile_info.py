from flask import *
from flask_sqlalchemy import SQLAlchemy
from data_base import *
from token_check import *
import datetime
"""
Upload a profile picture to 
a specific user.
"""
@app.route("/upload/profile/pic" , methods = ["POST"])
@jwt_required
def upload_profile_pic():
    if not check_token() == True:
        return check_token()
    email = request.form.get('email')
    profile_pic = request.form.get('profilePic')

    user = login_deatails_db.query.filter_by(email=email).first()
    if not user:
        return "",400
    username = user.user_info

    user = User_db.query.filter_by(username=str(username[0])).first()
    if not user:
        return "", 400


    user.profile_picture = str(profile_pic)        
    db.session.commit()
    return "", 200
"""
Adds a given location to a 
specific user.
"""
@app.route("/add_location",methods = ["POST"])
@jwt_required
def add_location():
    if not check_token() == True:
        return check_token()

    user_email = request.form.get('email')
    user_location = request.form.get('location')

    if not user_email or not user_location:
        return "", 400

    user = login_deatails_db.query.filter_by(email=user_email).first()
    username = user.user_info

    user = User_db.query.filter_by(username=str(username[0])).first()
    if not user:
        return "", 400

    user.location = str(user_location)
    db.session.commit()

    return "", 200

"""
Add a "meme" to the relation betwen user and Upload_db
"""
@app.route("/upload/meme" , methods = ["POST"])
@jwt_required
def upload_meme():
    if not check_token() == True:
        return check_token()
    email = request.form.get('email')
    memePic = request.form.get('memePic')
    memeTitle = request.form.get("memeTitle")

    user = login_deatails_db.query.filter_by(email=email).first()
    if not user or not memePic or Upload_db.query.filter_by(meme=memePic).first():
        return "", 400

    upload_meme = Upload_db(memeTitle=memeTitle,meme=memePic)
    user.upload_info.append(upload_meme)

    username = user.user_info
    user = User_db.query.filter_by(username=str(username[0])).first()
    user.time = datetime.datetime.now()
    user.newest_meme = memePic
    db.session.commit()
    return "", 200

"""
Gets a users entire profile.
"""
@app.route("/download/profile/info/<email>" , methods = ["GET"])
@jwt_required
def download_profile_pic(email):
    if not check_token() == True:
        return check_token()

    user = login_deatails_db.query.filter_by(email=email).first()
    username = user.user_info

    #user meme database. list od uploaded memes
    user_memes = user.upload_info

    user_db = User_db.query.filter_by(username=str(username[0])).first()
    location = str(user_db.location)
    profile_picture =  str(user_db.profile_picture)

    profile_dictionary = {"location": location, "profilePic": profile_picture, "allMemes": str(user_memes)}

    return jsonify(profile_dictionary), 200

