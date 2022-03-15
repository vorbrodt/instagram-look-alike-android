from flask import *
from flask_sqlalchemy import SQLAlchemy
import json
from token_check import *
from data_base import *
"""
Loginig out the user by putting 
the token in the black list db
"""
@app.route("/logout" , methods = ["POST"])
@jwt_required
def logout():
    if not check_token() == True:
        return check_token()
    btoken = request.headers.get('Authorization')
    bt = Blacklist_db(token=str(btoken))
    db.session.add(bt)
    db.session.commit()
    return "",200
