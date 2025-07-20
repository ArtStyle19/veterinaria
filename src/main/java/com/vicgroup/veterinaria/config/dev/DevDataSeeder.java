// src/main/java/com/vicgroup/veterinaria/config/DevDataSeeder.java
package com.vicgroup.veterinaria.config.dev;

import com.github.javafaker.Faker;
import com.vicgroup.veterinaria.core.enums.AccessLevelEnum;
import com.vicgroup.veterinaria.core.enums.PetStatusEnum;
import com.vicgroup.veterinaria.core.enums.SexEnum;
import com.vicgroup.veterinaria.core.enums.VisibilityEnum;
import com.vicgroup.veterinaria.modules.appointment.model.Appointment;
import com.vicgroup.veterinaria.modules.appointment.model.AppointmentSymptom;
import com.vicgroup.veterinaria.modules.appointment.repository.AppointmentRepo;
import com.vicgroup.veterinaria.modules.appointment.repository.AppointmentSymptomRepo;
import com.vicgroup.veterinaria.modules.clinic.model.Clinic;
import com.vicgroup.veterinaria.modules.clinic.repository.ClinicRepo;
import com.vicgroup.veterinaria.modules.pet.model.Pet;
import com.vicgroup.veterinaria.modules.pet.model.PetClinic;
import com.vicgroup.veterinaria.modules.pet.model.PetHistoricalRecord;
import com.vicgroup.veterinaria.modules.pet.repository.PetClinicRepo;
import com.vicgroup.veterinaria.modules.pet.repository.PetHistoricalRecordRepo;
import com.vicgroup.veterinaria.modules.pet.repository.PetRepo;
import com.vicgroup.veterinaria.modules.record.model.HistoricalRecordClinic;
import com.vicgroup.veterinaria.modules.record.repository.HistoricalRecordClinicRepo;
import com.vicgroup.veterinaria.modules.symptom.model.Symptom;
import com.vicgroup.veterinaria.modules.symptom.repository.SymptomRepo;
import com.vicgroup.veterinaria.modules.user.model.OwnerProfile;
import com.vicgroup.veterinaria.modules.user.model.Role;
import com.vicgroup.veterinaria.modules.user.model.User;
import com.vicgroup.veterinaria.modules.user.model.VetProfile;
import com.vicgroup.veterinaria.modules.user.repository.OwnerProfileRepo;
import com.vicgroup.veterinaria.modules.user.repository.RoleRepo;
import com.vicgroup.veterinaria.modules.user.repository.UserRepo;
import com.vicgroup.veterinaria.modules.user.repository.VetProfileRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Configuration
//@Profile("dev")                 // ⬅ solo cuando SPRING_PROFILES_ACTIVE=dev
@RequiredArgsConstructor
@Transactional          // ⬅️  todo el seeding ocurre en UNA sesión/tx
public class DevDataSeeder implements CommandLineRunner {

    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final ClinicRepo clinicRepo;
    private final VetProfileRepo vetRepo;
    private final OwnerProfileRepo ownerRepo;
    private final PetRepo petRepo;
    private final PetHistoricalRecordRepo histRepo;
    private final HistoricalRecordClinicRepo hrcRepo;
    private final AppointmentRepo appRepo;
    private final SymptomRepo symptomRepo;
    private final AppointmentSymptomRepo appSymRepo;
    private final PasswordEncoder encoder;
    private final PetClinicRepo petClinicRepo;   // ⬅️  nuevo
    final int CLINIC_COUNT = 15;          // antes 4
    final int VETS_PER_CLINIC = 4;       // antes 2
    final int OWNER_COUNT = 100;          // antes 12
    // DevDataSeeder.java  – al nivel de constantes
    private static final List<String> ALL_SYMPTOMS = List.of(
            "Acute blindness", "Urine infection", "Red bumps", "Loss of Fur",
            "Licking", "Grinning appearance", "Coughing", "Eye Discharge",
            "Seizures", "excess jaw tone", "Coma", "Weakness", "Wounds",
            "Neurological Disorders", "blood in stools", "Stiff and hard tail",
            "Dry Skin", "Lameness", "Swelling of gum", "Fever", "Bloated Stomach",
            "Face rubbing", "Aggression", "Wrinkled forehead", "Lumps", "Plaque",
            "Blindness", "Weight Loss", "Swollen Lymph nodes",
            "Excessive Salivation", "Loss of Consciousness", "Tender abdomen",
            "Purging", "Dandruff", "Loss of appetite", "Pale gums", "Collapse",
            "Constipation", "Hunger", "Discomfort", "Pain", "Paralysis",
            "Red patches", "Fur loss", "Losing sight", "WeightLoss", "Sepsis",
            "Increased drinking and urination", "Bad breath", "Itchy skin",
            "Receding gum", "Irritation", "Enlarged Liver", "Eating grass",
            "Nasal Discharge", "Depression", "lethargy", "Stiffness of muscles",
            "Eating less than usual", "Scratching", "Severe Dehydration",
            "Tartar", "Cataracts", "Swelling", "Redness of gum", "Diarrhea",
            "Scabs", "Breathing Difficulty", "Difficulty Urinating",
            "Continuously erect and stiff ears", "Glucose in urine", "Burping",
            "Passing gases", "Vomiting", "Blood in urine", "Smelly",
            "Redness around Eye area", "Bleeding of gum", "Bloody discharge",
            "Redness of skin", "Lethargy", "Abdominal pain", "Lack of energy",
            "Anorexia", "Heart Complication", "Yellow gums"
    );

    private final Faker faker = new Faker(new Locale("es"));
    @Override
    public void run(String... args) {

        /* ---------- ROLES ---------- */
        Map<String, Role> roles = Map.of(
                "ADMIN", ensureRole("ADMIN"),
                "VET",   ensureRole("VET"),
                "PET_OWNER", ensureRole("PET_OWNER")
        );

        /* ---------- CLÍNICAS ---------- */
        List<Clinic> clinics = new ArrayList<>();
        for (int i = 1; i <= CLINIC_COUNT; i++) {
            Clinic c = new Clinic();
            c.setName("ClinVet " + i);
            c.setAddress(faker.address().streetAddress());
            c.setLatitude(BigDecimal.valueOf(-12 + faker.number().randomDouble(4, 0, 10)));
            c.setLongitude(BigDecimal.valueOf(-77 + faker.number().randomDouble(4, 0, 10)));
            c.setEmail("info" + i + "@clinvet.com");
            clinics.add(clinicRepo.save(c));
        }

        /* ---------- USUARIOS ---------- */
        User admin = saveUser("admin", "123", roles.get("ADMIN"));

        List<VetProfile> vets = new ArrayList<>();
        for (int i = 0; i < CLINIC_COUNT*VETS_PER_CLINIC; i++) {
            Clinic clinic = clinics.get(i % clinics.size());
            User u = saveUser("vet" + (i + 1), "123", roles.get("VET"));

            VetProfile vp = new VetProfile();
            vp.setUser(u);
            vp.setClinic(clinic);
            vp.setEmail("vet" + (i + 1) + "@" + clinic.getName().toLowerCase() + ".com");
            vp.setCelNum(faker.phoneNumber().cellPhone());
            vets.add(vetRepo.save(vp));
        }

        List<OwnerProfile> owners = new ArrayList<>();
        for (int i = 0; i < OWNER_COUNT; i++) {
            User u = saveUser("owner" + (i + 1), "123", roles.get("PET_OWNER"));
            OwnerProfile op = new OwnerProfile();
            op.setUser(u);
            op.setEmail(u.getUsername() + "@mail.com");
            op.setCelNum(faker.phoneNumber().cellPhone());
            owners.add(ownerRepo.save(op));
        }

        /* ---------- SÍNTOMAS ---------- */
        List<String> baseSym = new ArrayList<>(List.of(
                "vomiting","lethargy","coughing","eye discharge","fever",
                "nasal discharge","loss of appetite","weight loss","diarrhea"
        ));



        /* ---------- SÍNTOMAS (persistir si faltan) ------------------- */
        for (String s : ALL_SYMPTOMS) {
            symptomRepo.findByName(s).orElseGet(() -> {
                Symptom sym = new Symptom();
                sym.setName(s);          // respeta mayúsculas/minúsculas
                return symptomRepo.save(sym);
            });
        }

        /* ---------- MASCOTAS / HISTORIAS / CITAS ---------- */
        Random rnd = new Random();

        for (OwnerProfile owner : owners) {
            int petsQty = rnd.nextInt(8) + 1; // 1-8 mascotas
            for (int p = 0; p < petsQty; p++) {

                Pet pet = new Pet();
                pet.setName(faker.dog().name());
                pet.setSpecies("Dog");
                pet.setBreed(faker.dog().breed());
                pet.setSex(rnd.nextBoolean() ? SexEnum.MALE : SexEnum.FEMALE);
                pet.setBirthdate(LocalDate.now().minusYears(rnd.nextInt(10) + 1));
                pet.setStatus(petsQty % 3 == 1 ? PetStatusEnum.OK : PetStatusEnum.LOST);
                pet.setOwner(owner.getUser());
                pet.setVisibility(VisibilityEnum.CLINIC_ONLY);
                pet.setEditCode(UUID.randomUUID().toString());

                // Asignar varias clínicas
                int clinicCount = rnd.nextInt(3) + 1; // 1–3 clínicas
                Set<Long> assignedClinics = new HashSet<>();
                while (assignedClinics.size() < clinicCount) {
                    assignedClinics.add(clinics.get(rnd.nextInt(clinics.size())).getId());
                }

                Long homeClinicId = assignedClinics.iterator().next(); // usar una como principal
                pet.setHomeClinicId(homeClinicId);
                pet = petRepo.save(pet);

                // Guardar relaciones pet-clinic
                for (Long clinicId : assignedClinics) {
                    if (!petClinicRepo.existsByPetIdAndClinicId(pet.getId(), clinicId)) {
                        PetClinic pc = new PetClinic();
                        pc.setPetId(pet.getId());
                        pc.setClinicId(clinicId);
                        petClinicRepo.save(pc);
                    }
                }

                // Crear historial
                PetHistoricalRecord record = new PetHistoricalRecord();
                record.setPet(pet);
                record.setCreatedBy(owner.getUser());
                record = histRepo.save(record);

                // Dar acceso WRITE a todas las clínicas asignadas
                for (Long clinicId : assignedClinics) {
                    HistoricalRecordClinic hrc = new HistoricalRecordClinic();
                    hrc.setRecordId(record.getId());
                    hrc.setClinicId(clinicId);
                    hrc.setAccessLevel(AccessLevelEnum.WRITE);
                    hrcRepo.save(hrc);
                }

                // Crear citas
                int apQty = rnd.nextInt(20) + 1;
                for (int a = 0; a < apQty; a++) {

                    // Elegir un vet aleatorio de una de las clínicas asignadas
                    List<Long> possibleClinics = new ArrayList<>(assignedClinics);
                    Long vetClinicId = possibleClinics.get(rnd.nextInt(possibleClinics.size()));

                    VetProfile vet = vets.stream()
                            .filter(v -> v.getClinic().getId().equals(vetClinicId))
                            .findAny()
                            .orElse(vets.get(0));

                    Appointment ap = new Appointment();
                    ap.setRecordId(record.getId());
                    ap.setDate(Instant.now().minusSeconds(86_400L * rnd.nextInt(400)));
                    ap.setWeight(BigDecimal.valueOf(4 + rnd.nextDouble() * 20).setScale(2, BigDecimal.ROUND_HALF_UP));
                    ap.setTemperature(BigDecimal.valueOf(38 + rnd.nextDouble()).setScale(1, BigDecimal.ROUND_HALF_UP));
                    ap.setHeartRate((short) (70 + rnd.nextInt(50)));
                    ap.setDescription("Chequeo de rutina");
                    ap.setDiagnosis("healthy");
                    ap.setCreatedBy(vet.getUser());
                    ap = appRepo.save(ap);

                    // Agregar síntomas
                    int sCount = rnd.nextInt(5) + 1;
                    List<String> pool = new ArrayList<>(ALL_SYMPTOMS);
                    Collections.shuffle(pool, rnd);
                    for (int s = 0; s < sCount; s++) {
                        Symptom sym = symptomRepo.findByName(pool.get(s)).orElseThrow();
                        AppointmentSymptom link = new AppointmentSymptom();
                        link.setAppointmentId(ap.getId());
                        link.setSymptomId(sym.getId());
                        appSymRepo.save(link);
                    }
                }
            }
        }


        System.out.println("\n🌱  DEV DB seeded:");
        System.out.printf("   • Clinics ...... %d%n", clinicRepo.count());
        System.out.printf("   • Vets ......... %d%n", vetRepo.count());
        System.out.printf("   • Owners ....... %d%n", ownerRepo.count());
        System.out.printf("   • Pets ......... %d%n", petRepo.count());
        System.out.printf("   • Appointments . %d%n%n", appRepo.count());
    }

    /* ---------- helpers ---------- */
    private Role ensureRole(String name) {
        return roleRepo.findByName(name).orElseGet(() -> {
            Role r = new Role();
            r.setName(name);
            return roleRepo.save(r);
        });
    }

    private User saveUser(String username, String rawPass, Role role) {
        return userRepo.findByUsername(username).orElseGet(() -> {
            User u = new User();
            u.setUsername(username);
            u.setPasswordHash(encoder.encode(rawPass));
            u.setRole(role);
            return userRepo.save(u);
        });
    }
}
