import xml.etree.ElementTree as ET
import psycopg2
import os

# PostgreSQL database credentials
db_host = "127.0.0.1"
db_name = "AODB Database"
db_user = "postgres"
db_password = "NewPassword123"


def insert_and_display_data(conn):
    try:
        cursor = conn.cursor()

        # Example LegData insertion
        leg_data_sql = """
            INSERT INTO LegData (FlightLegID)
            VALUES (%s);
        """
        leg_data_values = ('FL123',)
        cursor.execute(leg_data_sql, leg_data_values)

        # Commit the insertion before fetching
        conn.commit()

        # Display all data from LegData
        select_sql = "SELECT * FROM LegData;"
        cursor.execute(select_sql)
        rows = cursor.fetchall()

        if rows:
            print("Data in LegData table:")
            for row in rows:
                print(row)  # Or format as needed
        else:
            print("No data found in LegData table.")

    except psycopg2.Error as e:
        print(f"Database error: {e}")
        conn.rollback()  # Rollback in case of error
    finally:
        if cursor:
            cursor.close()


# Example usage (assuming you have a 'conn' object):


try:
    conn = psycopg2.connect(host=db_host, database=db_name, user=db_user, password=db_password)
    insert_and_display_data(conn)
    conn.close()  # Close the connection after you're done
except psycopg2.Error as e:
    print(f"Connection error: {e}")

