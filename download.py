#!/usr/bin/env python3
#  Copyright (c) 2021-2022 kb1000.
#
#  This Source Code Form is subject to the terms of the Mozilla Public
#  License, v. 2.0. If a copy of the MPL was not distributed with this
#  file, You can obtain one at http://mozilla.org/MPL/2.0/.

import hashlib
import os
import pathlib
import platform
import zipfile

import requests

mc_version = "1.17.1"
libraries = pathlib.Path("libraries")
natives = pathlib.Path("natives")
cwd = pathlib.Path("")

if platform.system() != "Linux": raise Exception("Only Linux is supported for now")


def download(session: requests.Session, target: pathlib.Path, file: dict):
    os.makedirs(target.parent, exist_ok=True)
    if target.exists():
        with open(target, "rb") as fp:
            s = fp.read()
        if len(s) == file["size"] and hashlib.sha1(s).hexdigest() == file["sha1"]:
            return

    with session.get(file["url"]) as resp:
        s = resp.content
        hexdigest = hashlib.sha1(s).hexdigest()
        if len(s) != file["size"] and hexdigest != file["sha1"]:
            raise Exception(
                f"File size is {len(s)}, expected {file['size']}, SHA1 is {hexdigest}, expected {file['sha1']}")
        with open(target, "wb") as fp:
            fp.write(s)


def main():
    with requests.session() as session:
        with session.get("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json") as resp:
            for version in resp.json()["versions"]:
                if version["id"] == mc_version:
                    version_url = version["url"]
        with session.get(version_url) as resp:
            version = resp.json()
        download(session,
                 libraries / "com" / "mojang" / "minecraft" / mc_version / f"minecraft-{mc_version}-client.jar",
                 version["downloads"]["client"])
        download(session,
                 libraries / "com" / "mojang" / "minecraft" / mc_version / f"minecraft-{mc_version}-server.jar",
                 version["downloads"]["server"])
        for library in version["libraries"]:
            download(session, libraries / library["downloads"]["artifact"]["path"], library["downloads"]["artifact"])
            if "classifiers" in library["downloads"] and "natives-linux" in library["downloads"]["classifiers"]:
                download(session, libraries / library["downloads"]["classifiers"]["natives-linux"]["path"], library["downloads"]["classifiers"]["natives-linux"])
                with zipfile.ZipFile(libraries / library["downloads"]["classifiers"]["natives-linux"]["path"], "r") as zf:
                    zf.extractall(natives, filter(lambda n: not n.startswith("META-INF") and "module-info.class" not in n, zf.namelist()))
        hashed = cwd / f"hashed-{mc_version}.jar"
        if not hashed.exists():
            with session.get(f"https://maven.quiltmc.org/repository/release/org/quiltmc/hashed/{mc_version}/hashed-{mc_version}.jar") as resp:
                with open(hashed, "wb") as fp:
                    fp.write(resp.content)


        os.makedirs(natives, exist_ok=True)

        with open("libs.txt", "r", encoding="ascii") as fp:
            for library in fp:
                library = library.strip()
                file = cwd / library.rpartition("/")[2]
                if not file.exists():
                    with session.get(library) as resp:
                        with open(file, "wb") as fp:
                            fp.write(resp.content)



if __name__ == '__main__':
    main()
