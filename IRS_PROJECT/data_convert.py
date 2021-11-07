import pymysql, random

db = pymysql.connect(host='junhao.io', port=3306, user='crypto', passwd='iss2021', db='crypto',  charset='utf8mb4')
cursor = db.cursor()

cursor.execute('SELECT UserName, ItemName FROM PlaybackActivity')
data = list(cursor.fetchall())

user_set = set()
item_set = set()

for ele in data:
	user_set.add(ele[0])
	item_set.add(ele[1])

# establish ids for users and items
user_id_dict = {}
item_id_dict = {}
count = 1
for user in list(user_set):
	user_id_dict[user] = str(count)
	count += 1
count = 1
for item in list(item_set):
	item_id_dict[item] = str(count)
	count += 1

def data_split(full_list, ratio, shuffle=True):

    n_total = len(full_list)
    offset = int(n_total * ratio)
    if n_total == 0 or offset < 1:
        return [], full_list
    if shuffle:
        random.shuffle(full_list)
    sublist_1 = full_list[:offset]
    sublist_2 = full_list[offset:]
    return sublist_1, sublist_2

train_data, test_data = data_split(data, ratio=0.7, shuffle=True) # train : test = 7 : 3

f = open('DATA/train', 'w+')
for record in train_data:
	f.write(user_id_dict[record[0]]+' '+item_id_dict[record[1]]+'\n')
f.close()

f = open('DATA/test', 'w+')
for record in test_data:
	f.write(user_id_dict[record[0]]+' '+item_id_dict[record[1]]+'\n')
f.close()