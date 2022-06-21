import os

files = os.listdir("registerEntries")

with open("lobby-register-dump", "w") as out:
    for file in files:
        with open("registerEntries/" + file, "r") as f:
            text = f.readline() + "\n"
            out.write(text)
