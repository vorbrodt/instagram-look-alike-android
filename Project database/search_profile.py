from flask import *
from flask_sqlalchemy import SQLAlchemy
from data_base import *
from token_check import *
"""
Gets a specific users display profile.
"""
@app.route("/username/search/<username>" , methods = ["GET"])
@jwt_required
def get_profile_info(username):
    if not check_token() == True:
        return check_token()
    user = User_db.query.filter_by(username=str(username)).first()
    if not user:
        return "", 400

    list_email =str([elm for elm in user.user_info][0])
    tupleInfo = eval(list_email)
    email = tupleInfo[0]


    display_account = login_deatails_db.query.filter_by(email=str(email)).first()
    memes = display_account.upload_info
    json_dictionary = {"location": user.location, "profilePic": user.profile_picture,"allMemes":str(memes)}
    return jsonify(json_dictionary)
