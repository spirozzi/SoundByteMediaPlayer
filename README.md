# SoundByte Media Player

## Introduction

SoundByte Media Player is a lightweight audio player written in Java. It is intended to have full support for MP3 and FLAC audio files. It can also read all versions of ID3 tags for MP3 files and Vorbis tags for FLAC files.

SoundByte allows you to import supported audio files to create a library. This allows for easy organization of files in addition to sorting based on song title, artist, album, etc.

## Distribution

SoundByte can be built with NetBeans IDE 8.0.2 (Windows platform) and Java JDK 1.7.

An executable jarfile alongside required libraries can be found in the dist folder of the repository. For more information regarding how NetBeans uses Apache Ant to automate the build process, see build.xml and dist/README.txt.

## Libraries (Java)

- Entagged (ID3 tag parsing)
- JFlac 1.3 (FLAC decoding, slightly modified for seekpoint creation and serialization)
- SQLite JDBC 3.7.2 (SQLite database support through JDBC)

## License

GNU GPL version 3
The full license text can be found in the LICENSE.txt file.
