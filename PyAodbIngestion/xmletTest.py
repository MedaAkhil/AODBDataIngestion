import xml.etree.ElementTree as ET

# Define the XML namespace
ns = {'yiapl': 'yiapl.co.in/root/schema', 'aidx': 'http://www.iata.org/IATA/2007/00'}

# Load and parse the XML file
xml_file = r"C:\Users\DELL\Desktop\Sravya\PyAodbIngestion\aodb_to_aip_adapter-2024-11-11-2\FlightLegNotifRQ_7.xml"
tree = ET.parse(xml_file)
root = tree.getroot()

def print_leg_data(root, ns):
    """Prints the LegData element and its children."""
    leg_data = root.find(".//aidx:FlightLeg/aidx:LegData", ns)  # Use XPath to find LegData
    if leg_data is not None:
        print("LegData:")
        for child in leg_data:
            print(f"  {child.tag}: {child.text.strip() if child.text else ''}")
            # Handle attributes if needed:
            for key, value in child.items():
                print(f"    {key}: {value}") #prints attributes within a tag
    else:
        print("LegData not found.")


print_leg_data(root, ns)
