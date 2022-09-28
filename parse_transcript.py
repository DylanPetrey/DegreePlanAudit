import PyPDF2
import nltk
import textract
import sys
import time
from nltk.tokenize import word_tokenize

# TODO
#   Output to file

# Function that checks if a string is a float
# This is used to filter out gpa-related values on the transcript
def isfloat(num):
    try:
        float(num)
        return True
    except:
        return False


def get_course_details(course):
    # Print the course identifier
    # These are always the first two tokens
    course_title = ' '.join(course[0:2])

    print(course_title, end='')

    # Trim course tokens
    course = course[2:]

    # Find the name of the course
    course_name = ''
    end_of_course_title = 0
    for c in course:
        if not isfloat(c):
            course_name += ' ' + c
            end_of_course_title += 1
        else:
            break
    print(course_name, end=' ')

    # Print semester identifier
    # This variable comes from the main program
    print(semester + ' x', end=' ') if not transfer else print('x ' + semester, end=' ')

    # Print grade
    # Grade is 2 tokens after the end of the course title
    # Grade is blank if the course is current
    print('x') if isfloat(course[end_of_course_title + 2]) else print(course[end_of_course_title + 2])


if __name__ == '__main__':
    # Checks for argument
    if not len(sys.argv) > 1:
        print("There is no file location in the argument")
    else:
        with open(str(sys.argv[1])):
            # Read in PDF
            pdfFileObj = open(sys.argv[1], 'rb')
            pdfReader = PyPDF2.PdfFileReader(pdfFileObj)

            num_pages = pdfReader.numPages
            count = 0
            text = ""  # The while loop will read each page.
            while count < num_pages:
                pageObj = pdfReader.getPage(count)
                count += 1
                text += pageObj.extractText()

            if text != "":
                text = text
            # If the above returns as False, we run the OCR library textract to
            # convert scanned/image based PDF files into text
            else:
                text = textract.process(sys.argv[1], method='tesseract', language='eng')
            # Close PDF
            pdfFileObj.close()

            tokens = []
            tokens = word_tokenize(text)

            # Print Name and ID
            name_pointer = 0
            id_pointer = 0
            date_pointer = 0

            # Extract Student Information
            for index in range(len(tokens)):
                # Stop the loop prematurely once all the information has been filled
                if name_pointer != 0 and id_pointer != 0 and date_pointer != 0:
                    break

                # Find the beginning of the Name
                if tokens[index:index+2] == ['Name', ':'] and name_pointer == 0:
                    name_pointer = index

                # Find the beginning of the ID
                # ID occurs after the name so we can use that as an endpoint to extract the names
                if tokens[index:index+3] == ['Student', 'ID', ':'] and id_pointer == 0:
                    id_pointer = index

                    # Print out all of the student's names
                    # Checks each token for a proper noun and then prints it out as the name
                    for n in [token[0] for token in nltk.pos_tag(tokens[name_pointer:id_pointer]) if token[1] == 'NNP']:
                        print(n, end=' ')
                    print()

                    # Validate ID length
                    if len(tokens[id_pointer + 3]) == 10:
                        print(tokens[id_pointer + 3])
                    else:
                        print('xxxxxxxxxx')

                # Print the date
                if tokens[index:index+3] == ['Active', 'in', 'Program'] and date_pointer == 0:
                    date_pointer = index
                    print(tokens[date_pointer-2])

            # initialize course variables
            semester = ''
            courses = []
            left = 0
            seasons = ['Fall', 'Summer', 'Spring']
            transfer = False

            start_pointer = 0

            # Get course information
            for i in range(len(tokens)):
                # Check for transfer
                if tokens[i:i+2] == ['Transfer', 'Credits']:
                    transfer = True
                if tokens[i:i+2] == ['Beginning', 'of'] and tokens[i+3] == 'Record':
                    transfer = False

                # Check for semester year
                if tokens[i] in seasons:
                    # Get string for semester identifier
                    semester = tokens[i - 1][-2:] + tokens[i][:2].upper()
                    if semester[2] == 'F':
                        semester = semester[:3]
                    print()
                    left = i+2

                # check for end of semester
                if tokens[i:i + 4] == ['Attempted', 'Earned', 'GPA', 'Uts'] or tokens[i:i+3] == ['Course', 'Trans', 'GPA']:
                    # extract tokens for the semester
                    schedule = tokens[left:i]

                    course_tokens = [[]]
                    line = 0
                    float_counter = 0
                    for j in range(len(schedule)):
                        # Move to next line
                        if float_counter > 3:
                            line += 1
                            float_counter = 0
                            course_tokens.append([])

                        if float_counter == 0 and not course_tokens[line]:
                            if len(schedule[j]) == 4 and (schedule[j].isdigit() or (schedule[j][0].isdigit() and schedule[j][1] == 'V' and schedule[j][2:4].isdigit())):
                                course_tokens[line].append(schedule[j - 1])
                            else:
                                continue

                        course_tokens[line].append(schedule[j])

                        # keeps track of where to end file
                        if isfloat(schedule[j]) or (schedule[j][0].isdigit() and schedule[j][1] == 'V' and schedule[j][2:4].isdigit()):
                            float_counter += 1

                    # Trim excess empty array off the back
                    if not course_tokens[-1]:
                        course_tokens = course_tokens[:-1]

                    # Print relevant course contents
                    for c in course_tokens:
                        get_course_details(c)

                    # Stops from looping a second time
                    left = i
    endTime = time.perf_counter()
