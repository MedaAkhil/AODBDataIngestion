import xml.etree.ElementTree as ET
import psycopg2
import os

# PostgreSQL database credentials
db_host = "127.0.0.1"
db_name = "AODB Database"
db_user = "postgres"
db_password = "NewPassword123"


def get_element_text(element):
    return element.text if element is not None else None

def get_element_attribute(element, attribute):
    return element.get(attribute) if element is not None else None


def insert_leg_data(conn, leg_data):
    sql = """
        INSERT INTO LegData (FlightLegID, AirlineCode, FlightNumber, DepartureAirport, 
                            DepartureAirportCodeContext, ArrivalAirport, ArrivalAirportCodeContext, 
                            OriginDate, InternationalStatus, OperationalStatus, ServiceType, 
                            AircraftType, AircraftSubType, AODBFlightID)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);
    """
    with conn.cursor() as cur:
        cur.execute(sql, leg_data)
    conn.commit()


def insert_airport_resources(conn, airport_resources):
    sql = """
        INSERT INTO AirportResources (FlightLegID, Usage, DepartureOrArrival, AircraftTerminal, PublicTerminal) 
        VALUES (%s, %s, %s, %s, %s);
    """
    with conn.cursor() as cur:
        cur.execute(sql, airport_resources)
    conn.commit()


def insert_operation_time(conn, operation_time):
    sql = """
        INSERT INTO OperationTime (FlightLegID, OperationalQualifier, TimeType, OperationTimeValue)
        VALUES (%s, %s, %s, %s);
    """
    with conn.cursor() as cur:
        cur.execute(sql, operation_time)
    conn.commit()


def process_xml_file(filepath, conn):
    try:
        tree = ET.parse(filepath)
        root = tree.getroot()
        print("this is root:",root.find("IATA_AIDX_FlightLegNotifRQ"))
        ns = {'yiapl': 'yiapl.co.in/root/schema', 'aidx': 'http://www.iata.org/IATA/2007/00'}

        flight_leg = root.find("aidx:IATA_AIDX_FlightLegNotifRQ/aidx:FlightLeg", ns)
        if not flight_leg:
            print(f"Skipping file {filepath}: FlightLeg not found")
            return

        leg_identifier = flight_leg.find("aidx:LegIdentifier", ns)
        leg_data = flight_leg.find("aidx:LegData", ns)
        airport_resources = leg_data.find("aidx:AirportResources", ns) if leg_data else None
        resource = airport_resources.find("aidx:Resource", ns) if airport_resources else None
        operation_time_element = leg_data.find("aidx:OperationTime", ns) if leg_data else None
        tpa_extension = flight_leg.find("aidx:TPA_Extension", ns)

        flight_leg_id = get_element_text(tpa_extension.find("yiapl:AODBFlightID", ns))

        leg_data_to_insert = (
            flight_leg_id,
            get_element_text(leg_identifier.find("aidx:Airline", ns)),
            int(get_element_text(leg_identifier.find("aidx:FlightNumber", ns)) or 0),
            get_element_text(leg_identifier.find("aidx:DepartureAirport", ns)),
            get_element_attribute(leg_identifier.find("aidx:DepartureAirport", ns), "CodeContext"),
            get_element_text(leg_identifier.find("aidx:ArrivalAirport", ns)),
            get_element_attribute(leg_identifier.find("aidx:ArrivalAirport", ns), "CodeContext"),
            get_element_text(leg_identifier.find("aidx:OriginDate", ns)),
            get_element_attribute(leg_data, "InternationalStatus"),
            get_element_text(leg_data.find("aidx:OperationalStatus", ns)),
            get_element_text(leg_data.find("aidx:ServiceType", ns)),
            get_element_text(leg_data.find("aidx:AircraftInfo/aidx:AircraftType", ns)),
            get_element_text(leg_data.find("aidx:AircraftInfo/aidx:AircraftSubType", ns)),
            flight_leg_id,
        )

        airport_resources_to_insert = (
            flight_leg_id,
            get_element_attribute(airport_resources, "Usage"),
            get_element_attribute(resource, "DepartureOrArrival"),
            get_element_text(resource.find("aidx:AircraftTerminal", ns)) if resource else None,
            get_element_text(resource.find("aidx:PublicTerminal", ns)) if resource else None,

        )

        operation_time_to_insert = (
            flight_leg_id,
            get_element_attribute(operation_time_element, "OperationQualifier"),
            get_element_attribute(operation_time_element, "TimeType"),
            get_element_text(operation_time_element),
        )


        insert_leg_data(conn, leg_data_to_insert)
        if all(airport_resources_to_insert): # Check if tuple is not entirely None or empty strings.
            insert_airport_resources(conn, airport_resources_to_insert)
        if all(operation_time_to_insert):
            insert_operation_time(conn, operation_time_to_insert)



    except ET.ParseError as e:
        print(f"Error parsing XML file {filepath}: {e}")
    except psycopg2.Error as e:
        print(f"Database error: {e}")


def main(root_folder):
    try:
        conn = psycopg2.connect(host=db_host, database=db_name, user=db_user, password=db_password)
        print("Connected to PostgreSQL database")

        for subdir, _, files in os.walk(root_folder):
            for file in files:
                if file.endswith(".xml"):
                    filepath = os.path.join(subdir, file)
                    print(f"Processing: {filepath}")
                    process_xml_file(filepath, conn)

        conn.close()
        print("Disconnected from PostgreSQL database")

    except psycopg2.Error as e:
        print(f"Error connecting to database: {e}")


root_folder = r"c:\Users\Sravya Reddy\OneDrive\Desktop\AODBIngestion\aodb_to_aip_adapter-2024-11-11-2"
main(root_folder)

