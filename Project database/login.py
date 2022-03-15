from flask import *
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy.orm import load_only
import json
from data_base import *
import bcrypt

"""
login and creats a token on success
"""
@app.route("/login", methods = ["POST"])
def login():
    input_email = request.form.get('email')
    input_password = request.form.get('password')
    #cryptering
    user_db = login_deatails_db.query.filter_by(email=input_email).first()
    if not user_db:
        return "",400
    salt = user_db.salt
    hased_password = bcrypt.hashpw(str(input_password).encode('utf8'),salt)
    if login_deatails_db.query.filter_by(email=input_email, password=hased_password).all():
        token = create_access_token(identity=str(login_deatails_db.email))
        return jsonify(access_token=token), 200
    else:
        return "Wrong username or password. Please try again!", 400
