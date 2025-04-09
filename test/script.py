import os
import pandas as pd

# Function to get all CSV files from a directory
def get_csv_files_from_directory(directory):
    return [f for f in os.listdir(directory) if f.endswith('.csv')]

# Function to append CSV files
def append_csv_files(directory):
    # Get the list of CSV files in the directory
    csv_files = get_csv_files_from_directory(directory)
    
    if len(csv_files) < 2:
        print("Not enough CSV files to append.")
        return

    # Sort files alphabetically or according to any other logic
    csv_files.sort()

    # First file (will hold all the appended data)
    first_file = os.path.join(directory, csv_files[0])
    
    for file in csv_files[1:]:
        current_file = os.path.join(directory, file)
        
        # Read the current CSV file without header (skip first row)
        data = pd.read_csv(current_file, header=0)
        
        # Append the data to the first file
        data.to_csv(first_file, mode='a', header=False, index=False)
        
        # Delete the current file after appending
        os.remove(current_file)
        print(f"{current_file} has been appended and deleted.")

    print("All files have been appended successfully!")

# Example usage: pass the directory containing your CSV files
directory_path = './datasets_novos'  # Replace with your directory path
append_csv_files(directory_path)
