from flask import Flask, render_template, request, jsonify
import random
import unicodedata

app = Flask(__name__)

# Flag to return when the answer is true
FLAG = "CSCC{f1h4_un1c0de_r38re8f8df88we4!}"

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/check', methods=['POST'])
def check():
    fiha = request.form.get('fiha')
    obvious ={'true','0','1','false'}
    if fiha in obvious:
        return jsonify({'success': False, 'note':'Monkey testing lol'})
    if 'fiha' in fiha:
        return jsonify({'success': False, 'note':'Blocked word'})
    
    normalized = unicodedata.normalize('NFKD', fiha)
    if 'fiha' in normalized:
        return jsonify({'success': True,'note':'Random testing paid out eventually', 'flag': FLAG})
    else:
        return jsonify({'success': False})

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
