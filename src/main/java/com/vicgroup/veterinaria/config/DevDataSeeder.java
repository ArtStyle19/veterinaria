// src/main/java/com/vicgroup/veterinaria/config/DevDataSeeder.java
package com.vicgroup.veterinaria.config;

import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vicgroup.veterinaria.model.*;
import com.vicgroup.veterinaria.model.enums.*;
import com.vicgroup.veterinaria.repository.*;

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
    final int CLINIC_COUNT = 10;          // antes 4
    final int VETS_PER_CLINIC = 40;       // antes 2
    final int OWNER_COUNT = 50;          // antes 12
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
        Map<String,Role> roles = Map.of(
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
            int petsQty = rnd.nextInt(8) + 1;            // 1-2 mascotas
            for (int p = 0; p < petsQty; p++) {

                Pet pet = new Pet();
                pet.setName(faker.dog().name());
                pet.setSpecies("Dog");
                pet.setBreed(faker.dog().breed());
                pet.setSex(rnd.nextBoolean() ? SexEnum.MALE : SexEnum.FEMALE);
                pet.setBirthdate(LocalDate.now().minusYears(rnd.nextInt(10) + 1));
                pet.setStatus(petsQty % 3 == 1 ? PetStatusEnum.OK : PetStatusEnum. LOST);
                pet.setOwner(owner.getUser());
                pet.setHomeClinicId(clinics.get(rnd.nextInt(clinics.size())).getId());
                pet.setVisibility(VisibilityEnum.CLINIC_ONLY);
                pet.setEditCode(UUID.randomUUID().toString());
                pet = petRepo.save(pet);


//                pet = petRepo.save(pet);

                /* ➜ Añadir: link Pet-Clinic */
                if (!petClinicRepo.existsByPetIdAndClinicId(pet.getId(), pet.getHomeClinicId())) {
                    PetClinic pc = new PetClinic();
                    pc.setPetId(pet.getId());
                    pc.setClinicId(pet.getHomeClinicId());
                    petClinicRepo.save(pc);
                }
                // historial
                PetHistoricalRecord record = new PetHistoricalRecord();
                record.setPet(pet);
                record.setCreatedBy(owner.getUser());
                record = histRepo.save(record);

                // acceso home-clinic
                HistoricalRecordClinic hrc = new HistoricalRecordClinic();
                hrc.setRecordId(record.getId());
                hrc.setClinicId(pet.getHomeClinicId());
                hrc.setAccessLevel(AccessLevelEnum.WRITE);
                hrcRepo.save(hrc);

                // 1-3 citas
                int apQty = rnd.nextInt(12) + 1;
                for (int a = 0; a < apQty; a++) {

                    Pet finalPet = pet;
                    VetProfile vet = vets.stream()
                            .filter(v -> v.getClinic().getId().equals(finalPet.getHomeClinicId()))
                            .findAny().orElse(vets.get(0));

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

                    /* síntomas */
// dentro del bucle de citas
// dentro del bucle de citas  (reemplaza el bloque antiguo)
                    int sCount = rnd.nextInt(5) + 1;                 // 1-5 síntomas distintos
                    List<String> pool = new ArrayList<>(ALL_SYMPTOMS);   // ← copia mutable
                    Collections.shuffle(pool, rnd);                     // ok ahora
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
