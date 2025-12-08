The goal for this app is to help people to find locations to discard items that can't be thrown away in regular disposal, such as cooking oil, batteries, and car tires.

The initial database for location data is initialized with data from a JSON named location_data.csv in the res/raw directory.

The code for loading and managing location data is in the data/DataSource.kt file.

The location data is stored in a Room database where the table name is "locais"