import os
import xml.etree.ElementTree as ET
import mysql.connector  # Use mysql.connector for MySQL

# Database credentials (replace with your MySQL credentials)
mydb = mysql.connector.connect(
    host="localhost",
    user="root",
    password="12345678",
    database="aodbflightdata"  # Ensure this database exists
)

# XML namespace
ns = {'yiapl': 'yiapl.co.in/root/schema', 'aidx': 'http://www.iata.org/IATA/2007/00'}

# Root directory containing multiple subfolders with XML files
root_dir = r"C:\Users\DELL\Desktop\Sravya\TestDataPython"

def insert_data(mydb, table_name, data_dict):
    """Insert data into the specified database table."""
    try:
        mycursor = mydb.cursor()

        columns = ", ".join([f"`{col}`" for col in data_dict.keys()])  # Escape column names
        placeholders = ", ".join(["%s"] * len(data_dict))
        sql = f"INSERT INTO {table_name} ({columns}) VALUES ({placeholders})"

        values = tuple(data_dict.values())
        mycursor.execute(sql, values)
        mydb.commit()
        print(f"Record inserted into {table_name}: {mycursor.rowcount}")

    except mysql.connector.Error as err:
        print(f"Error inserting into {table_name}: {err}")
        mydb.rollback()
    finally:
        if mycursor:
            mycursor.close()

def process_xml_and_insert(mydb, xml_file, ns):
    """Process a single XML file and insert data into the database."""
    try:
        tree = ET.parse(xml_file)
        root = tree.getroot()
        flight_leg = root.find(".//aidx:FlightLeg", ns)  # Find the FlightLeg element

        if flight_leg is not None:
            leg_data_element = flight_leg.find("aidx:LegData", ns)
            airport_resources_element = leg_data_element.find("aidx:AirportResources", ns) if leg_data_element is not None else None
            aircraftinfo_element = leg_data_element.find("aidx:AircraftInfo", ns) if leg_data_element is not None else None

            if leg_data_element is not None:
                leg_data = {}
                for child in leg_data_element:
                    tag_name = child.tag.split('}')[1]
                    if tag_name not in ["AirportResources", "AircraftInfo"]:  # Exclude these elements
                        if tag_name == "CabinClass":
                            leg_data["CabinClass_Class"] = child.get("Class")
                        elif tag_name == "AssociatedFlightLegSchedule":
                            leg_data["AssociatedFlightLegSchedule_RepeatIndex"] = child.get("RepeatIndex")
                        else:
                            leg_data[tag_name] = child.text.strip() if child.text else None
                            for key, value in child.items():
                                leg_data[f"{tag_name}_{key}"] = value
                insert_data(mydb, "LegData", leg_data)

            if airport_resources_element is not None:
                for resource in airport_resources_element.findall("aidx:Resource", ns):  # Handle multiple resources
                    airport_resources = {}

                    # Extracting child elements
                    for child in resource:
                        tag_name = child.tag.split('}')[1]
                        airport_resources[tag_name] = child.text.strip() if child.text else None

                    # Extracting attributes of `Resource`
                    airport_resources["Resource_DepartureOrArrival"] = resource.get("DepartureOrArrival")
                    airport_resources["Resource_RepeatIndex"] = resource.get("RepeatIndex")

                    # Extracting attributes from `AirportResources`
                    airport_resources["Usage"] = airport_resources_element.get("Usage")
                    airport_resources["AirportResources_RepeatIndex"] = airport_resources_element.get("RepeatIndex")

                    insert_data(mydb, "AirportResources", airport_resources)

            if aircraftinfo_element is not None:
                aircraftinfo = {}
                for child in aircraftinfo_element:
                    tag_name = child.tag.split('}')[1]
                    aircraftinfo[tag_name] = child.text.strip() if child.text else None

                insert_data(mydb, "AircraftInfo", aircraftinfo)

        else:
            print(f"FlightLeg not found in XML: {xml_file}")

    except Exception as e:
        print(f"Error processing file {xml_file}: {e}")

def process_all_xml_files(mydb, root_dir, ns):
    """Recursively find and process all XML files in subdirectories."""
    for subdir, _, files in os.walk(root_dir):
        for file in files:
            if file.endswith(".xml"):  # Only process XML files
                xml_file_path = os.path.join(subdir, file)
                print(f"Processing: {xml_file_path}")
                process_xml_and_insert(mydb, xml_file_path, ns)

# Run the processing function for all XML files in the root directory
process_all_xml_files(mydb, root_dir, ns)

# Close the database connection
mydb.close()
