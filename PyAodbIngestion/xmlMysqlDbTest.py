import xml.etree.ElementTree as ET
import mysql.connector  # Use mysql.connector for MySQL

# Database credentials (replace with your MySQL credentials)
mydb = mysql.connector.connect(
    host="localhost",
    user="root",
    password="12345678",
    database="aodbflightdata"  # Make sure this database exists
)

# XML namespace
ns = {'yiapl': 'yiapl.co.in/root/schema', 'aidx': 'http://www.iata.org/IATA/2007/00'}

# XML file path
xml_file = r"C:\Users\DELL\Desktop\Sravya\PyAodbIngestion\aodb_to_aip_adapter-2024-11-11-2\FlightLegNotifRQ_8.xml"

def insert_leg_data(mydb, leg_data_dict):
    try:
        mycursor = mydb.cursor()

        # Construct column names and placeholders dynamically
        columns = ", ".join(leg_data_dict.keys())
        placeholders = ", ".join(["%s"] * len(leg_data_dict))
        sql = f"INSERT INTO LegData ({columns}) VALUES ({placeholders})"

        values = tuple(leg_data_dict.values())
        mycursor.execute(sql, values)
        mydb.commit()
        print(f"Record inserted: {mycursor.rowcount}")

    except mysql.connector.Error as err:
        print(f"Error: {err}")
        mydb.rollback()
    finally:
        if mycursor:
            mycursor.close()

def insert_airport_resources(ele,airport_dict):
    pass

    
def process_xml_and_insert(mydb, xml_file, ns):
    tree = ET.parse(xml_file)
    root = tree.getroot()
    leg_data_element = root.find(".//aidx:FlightLeg/aidx:LegData", ns)

    if leg_data_element is not None:
        leg_data = {}
        for child in leg_data_element:
            tag_name = child.tag.split('}')[1]

            # Skip AirportResources and its children
            if tag_name in ["AirportResources","AircraftInfo"]:
                continue  # Skip this element and go to the next sibling

            # Existing code for other elements
            if tag_name == "CabinClass":
                leg_data["CabinClass_Class"] = child.get("Class")
            elif tag_name == "AssociatedFlightLegSchedule":
                leg_data["AssociatedFlightLegSchedule_RepeatIndex"] = child.get("RepeatIndex")
            else:
                leg_data[tag_name] = child.text.strip() if child.text else None
                for key, value in child.items(): #handle attributes 
                    leg_data[f"{tag_name}_{key}"] = value

        insert_leg_data(mydb, leg_data)  # Insert the filtered data

        # call the insert_airport_resources only if their is an xml tag aidx:AirportResources in the given xml file
        


    else:
        print("LegData not found in XML.")



    






process_xml_and_insert(mydb, xml_file, ns)


mydb.close()  # Close the connection after all operations
