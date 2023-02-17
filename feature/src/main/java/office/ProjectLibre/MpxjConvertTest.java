package office.ProjectLibre;

import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskContainer;
import net.sf.mpxj.reader.ProjectReader;
import net.sf.mpxj.reader.UniversalProjectReader;
import net.sf.mpxj.sample.MpxjConvert;
import net.sf.mpxj.writer.ProjectWriter;
import net.sf.mpxj.writer.ProjectWriterUtility;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MpxjConvertTest {

    public static void main(String[] args) throws Exception {

        if (args == null || args.length == 0 || args[0] == null || args[0].isEmpty()) {
            System.out.println("project file args not provide!");
            return;
        }

        File inputFile = new File(args[0]);
        if (!inputFile.exists()) {
            System.out.println("project file path not exist!");
            return;
        }

        String absolutePath = inputFile.getAbsolutePath();
        int lastDotIndex = absolutePath.lastIndexOf(".");
        String removeExtensionName = absolutePath.substring(0, lastDotIndex);

//        ProjectReader reader = new ProjectLibreReader();
        ProjectReader reader = new UniversalProjectReader();
        ProjectFile projectFile = reader.read(inputFile);

        System.out.println("########################### editConstraintTypeTest ###########################");
        editConstraintTypeTest(projectFile, removeExtensionName);

        System.out.println("########################### convertTest ###########################");
//        convertTest(inputFile, removeExtensionName);

    }

    private static void editConstraintTypeTest(ProjectFile projectFile, String removeExtensionName) throws InstantiationException, IllegalAccessException, IOException {
        TaskContainer tasks = projectFile.getTasks();
        for (Task task : tasks) {
            ConstraintType constraintType = task.getConstraintType();
            if (constraintType != ConstraintType.AS_SOON_AS_POSSIBLE) {
                continue;
            }

            Integer uniqueID = task.getUniqueID();
            Integer id = task.getID();
            String name = task.getName();

            // on ms project, it may be null
            // on projectlibre, it is default to 1970.
            Date constraintDate = task.getConstraintDate();
            String constraintDateString = "<empty-constraint-date>";
            if (constraintDate != null) {
                constraintDateString = constraintDate.toString();
            }

            String resourceNames = "<empty-resource-names>";
            List<ResourceAssignment> resourceAssignments = task.getResourceAssignments();
            if (resourceAssignments != null) {
                resourceNames = resourceAssignments.stream()
                        .filter(Objects::nonNull)
                        .map(ResourceAssignment::getResource)
                        .filter(Objects::nonNull)
                        .map(Resource::getName)
                        .collect(Collectors.joining(","));
            }

            String format = String.format("uid=[%s], id=[%s], name=[%s], constraintDate=[%s], resourceNames=[%s], change constraintType=[%s] to [%s]!"
                    , uniqueID, id, name, constraintDateString, resourceNames, constraintType, ConstraintType.START_NO_EARLIER_THAN);
            System.out.println(format);

            task.setConstraintType(ConstraintType.START_NO_EARLIER_THAN);
        }

        projectFile.updateStructure();

        String editedFile = removeExtensionName + "-edited.xml";
        ProjectWriter projectWriter = ProjectWriterUtility.getProjectWriter(editedFile);
        projectWriter.write(projectFile, editedFile);
    }

    public static void convertTest(File inputFile, String removeExtensionName) throws Exception {
        File outputFile = new File(removeExtensionName + "-converted.xml");
//        ProjectWriter mspdiWriter = new MSPDIWriter();
//        write(projectFile, mspdiWriter, outputFile);
        process(inputFile, outputFile);

        outputFile = new File(removeExtensionName + "-converted.planner");
//        ProjectWriter plannerWriter = new PlannerWriter();
//        write(projectFile, plannerWriter, outputFile);
        process(inputFile, outputFile);

        outputFile = new File(removeExtensionName + "-converted.mpx");
//        ProjectWriter mpxWriter = new MPXWriter();
//        write(projectFile, mpxWriter, outputFile);
        process(inputFile, outputFile);
    }

    public static void write(ProjectFile projectFile, ProjectWriter writer, File outputFile) throws IOException {
        writer.write(projectFile, outputFile);
    }

    public static void process(File inputFile, File outputFile) throws Exception {
        MpxjConvert mpxjConvert = new MpxjConvert();
        mpxjConvert.process(inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
    }

}
